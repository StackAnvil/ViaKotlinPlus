plugins {
    id("configuration.transitive_jar_in_jar")
    id("via.maven_publish")
    id("base.junit")
    id("extra.unlock_build_errors")
}

// Comment during Minecraft updates to update data diff files
tasks.test {
    enabled = false
}

dependencies {
    jarInJar(projects.viafabricplusApi) {
        exclude("net.fabricmc", "fabric-loader")
    }

    jarInJar(platform(libs.fabric.api.bom))
    jarInJar(libs.fabric.api.base)
    jarInJar(libs.fabric.resource.loader.v1)
    jarInJar(libs.fabric.resource.loader.v0)
    jarInJar(libs.fabric.networking.api.v1)
    jarInJar(libs.fabric.command.api.v2)
    jarInJar(libs.fabric.lifecycle.events.v1)
    jarInJar(libs.fabric.particles.v1)
    jarInJar(libs.fabric.registry.sync.v0)

    jarInJar(libs.reflect)
    jarInJar(libs.classic4j)

    compileOnly(libs.modmenu)
}


subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions.freeCompilerArgs.addAll("-Xnullability-annotations=@org.jetbrains.annotations:ignore", "-Xjspecify-annotations=ignore")
    }
}

// Kotlin can only express static members through a companion object. Mixin
// rejects the Companion field and the static forwarders that delegate to it, so
// move companion methods onto the mixin class as real static methods, then
// normalise field visibility and initialisers to what Mixin expects.
tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    doLast {
        val classesDir = destinationDirectory.get().asFile
        val shadowDesc = "Lorg/spongepowered/asm/mixin/Shadow;"
        classesDir.walkTopDown().filter { it.isFile && it.extension == "class" }.forEach { file ->
            val node = org.objectweb.asm.tree.ClassNode()
            org.objectweb.asm.ClassReader(file.readBytes()).accept(node, 0)
            val mixin = (node.visibleAnnotations?.any { it.desc == "Lorg/spongepowered/asm/mixin/Mixin;" } == true) ||
                (node.invisibleAnnotations?.any { it.desc == "Lorg/spongepowered/asm/mixin/Mixin;" } == true)
            if (!mixin) return@forEach
            node.methods.removeIf { it.name.startsWith("viaFabricPlus\$body\$") }
            val isInterface = node.access and org.objectweb.asm.Opcodes.ACC_INTERFACE != 0
            val shadows = node.fields.filter { f ->
                (f.visibleAnnotations?.any { it.desc == shadowDesc } == true) ||
                    (f.invisibleAnnotations?.any { it.desc == shadowDesc } == true)
            }.map { it.name }.toSet()

            // Kotlin emits @JvmStatic handlers as static forwarders that call a
            // Companion instance. Mixin needs the real method on the mixin
            // class, so move the companion body to the mixin and keep a small
            // static bridge with the original signature (and annotations).
            val companionName = node.name + "\$Companion"
            val companionFile = File(classesDir, companionName.replace('.', '/') + ".class")
            if (companionFile.isFile) {
                val companion = org.objectweb.asm.tree.ClassNode()
                org.objectweb.asm.ClassReader(companionFile.readBytes()).accept(companion, 0)
                companion.methods.filter { it.name != "<init>" && it.name != "<clinit>" }.forEach { m ->
                    val wasStatic = m.access and org.objectweb.asm.Opcodes.ACC_STATIC != 0
                    val originalName = m.name
                    val originalDesc = m.desc
                    if (wasStatic) {
                        node.methods.removeAll { it.name == originalName && it.desc == originalDesc }
                        rewriteCompanionReferences(m, companionName, node.name, isInterface)
                        m.access = org.objectweb.asm.Opcodes.ACC_PRIVATE or org.objectweb.asm.Opcodes.ACC_STATIC
                        node.methods.add(m)
                    } else {
                        val type = org.objectweb.asm.Type.getMethodType(originalDesc)
                        val bodyName = "viaFabricPlus\$body\$" + (originalName + originalDesc).hashCode().toString().replace("-", "_")
                        val bodyDesc = org.objectweb.asm.Type.getMethodDescriptor(
                            type.returnType,
                            org.objectweb.asm.Type.getObjectType(companionName),
                            *type.argumentTypes
                        )
                        val annotationsVisible = m.visibleAnnotations
                        val annotationsInvisible = m.invisibleAnnotations
                        rewriteCompanionReferences(m, companionName, node.name, isInterface)
                        m.name = bodyName
                        m.desc = bodyDesc
                        m.access = org.objectweb.asm.Opcodes.ACC_PRIVATE or org.objectweb.asm.Opcodes.ACC_STATIC
                        m.visibleAnnotations = null
                        m.invisibleAnnotations = mutableListOf(
                            org.objectweb.asm.tree.AnnotationNode("Lorg/spongepowered/asm/mixin/Unique;")
                        )
                        node.methods.add(m)

                        val forwarder = node.methods.firstOrNull { it.name == originalName && it.desc == originalDesc }
                        val bridge = forwarder ?: org.objectweb.asm.tree.MethodNode(
                            org.objectweb.asm.Opcodes.ACC_PRIVATE or org.objectweb.asm.Opcodes.ACC_STATIC,
                            originalName, originalDesc, null, null
                        )
                        bridge.instructions.clear()
                        bridge.tryCatchBlocks.clear()
                        bridge.localVariables?.clear()
                        bridge.instructions.add(org.objectweb.asm.tree.InsnNode(org.objectweb.asm.Opcodes.ACONST_NULL))
                        var local = 0
                        for (argument in type.argumentTypes) {
                            bridge.instructions.add(org.objectweb.asm.tree.VarInsnNode(argument.getOpcode(org.objectweb.asm.Opcodes.ILOAD), local))
                            local += argument.size
                        }
                        bridge.instructions.add(
                            org.objectweb.asm.tree.MethodInsnNode(
                                org.objectweb.asm.Opcodes.INVOKESTATIC, node.name, bodyName, bodyDesc, isInterface
                            )
                        )
                        bridge.instructions.add(org.objectweb.asm.tree.InsnNode(type.returnType.getOpcode(org.objectweb.asm.Opcodes.IRETURN)))
                        bridge.access = org.objectweb.asm.Opcodes.ACC_PRIVATE or org.objectweb.asm.Opcodes.ACC_STATIC
                        bridge.visibleAnnotations = annotationsVisible
                        bridge.invisibleAnnotations = annotationsInvisible
                        if (forwarder == null) node.methods.add(bridge)
                    }
                }
            }

            if (isInterface) {
                node.fields.clear()
                node.methods.removeIf { it.name == "<clinit>" }
                node.methods.forEach { m ->
                    if (m.access and org.objectweb.asm.Opcodes.ACC_STATIC != 0) {
                        m.access = (m.access and org.objectweb.asm.Opcodes.ACC_PUBLIC.inv()
                            and org.objectweb.asm.Opcodes.ACC_PROTECTED.inv()
                            and org.objectweb.asm.Opcodes.ACC_FINAL.inv()) or org.objectweb.asm.Opcodes.ACC_PRIVATE
                    }
                }
            } else {
                node.fields.forEach { f ->
                    if (f.access and org.objectweb.asm.Opcodes.ACC_STATIC != 0) {
                        f.access = (f.access and org.objectweb.asm.Opcodes.ACC_PUBLIC.inv()
                            and org.objectweb.asm.Opcodes.ACC_PROTECTED.inv()
                            and org.objectweb.asm.Opcodes.ACC_PRIVATE.inv()) or org.objectweb.asm.Opcodes.ACC_PRIVATE
                    }
                }
            }

            node.methods.forEach { method ->
                if (method.access and org.objectweb.asm.Opcodes.ACC_STATIC != 0 && method.name != "<clinit>") {
                    method.access = (method.access and org.objectweb.asm.Opcodes.ACC_PUBLIC.inv()
                        and org.objectweb.asm.Opcodes.ACC_PROTECTED.inv()
                        and org.objectweb.asm.Opcodes.ACC_PRIVATE.inv()) or org.objectweb.asm.Opcodes.ACC_PRIVATE
                }
                if ((method.name == "<clinit>" || method.name == "<init>") && shadows.isNotEmpty()) {
                    val it = method.instructions.iterator()
                    val toRemove = mutableListOf<org.objectweb.asm.tree.AbstractInsnNode>()
                    while (it.hasNext()) {
                        val insn = it.next()
                        if (insn is org.objectweb.asm.tree.FieldInsnNode &&
                            (insn.opcode == org.objectweb.asm.Opcodes.PUTSTATIC || insn.opcode == org.objectweb.asm.Opcodes.PUTFIELD) &&
                            insn.name in shadows
                        ) {
                            toRemove.add(insn)
                            var prev = insn.previous
                            while (prev != null &&
                                prev !is org.objectweb.asm.tree.LabelNode &&
                                prev !is org.objectweb.asm.tree.FrameNode &&
                                prev !is org.objectweb.asm.tree.LineNumberNode &&
                                !(prev is org.objectweb.asm.tree.FieldInsnNode && prev.opcode == org.objectweb.asm.Opcodes.PUTSTATIC) &&
                                !(prev is org.objectweb.asm.tree.MethodInsnNode && prev.owner == node.name && prev.name == "<init>")
                            ) {
                                toRemove.add(prev)
                                prev = prev.previous
                            }
                        }
                    }
                    toRemove.forEach { method.instructions.remove(it) }
                }
                if (method.name == "<init>" || method.name == "<clinit>") {
                    val it = method.instructions.iterator()
                    while (it.hasNext()) {
                        if (it.next().opcode == org.objectweb.asm.Opcodes.NOP) it.remove()
                    }
                }
            }

            val writer = org.objectweb.asm.ClassWriter(0)
            try {
                node.accept(writer)
                file.writeBytes(writer.toByteArray())
            } catch (t: Throwable) {
                println("[mixin-transform] failed ${node.name}: $t")
                throw t
            }
        }
    }
}


private fun rewriteCompanionReferences(
    method: org.objectweb.asm.tree.MethodNode,
    companionName: String,
    ownerName: String,
    ownerIsInterface: Boolean
) {
    method.instructions.forEach { insn ->
        if (insn is org.objectweb.asm.tree.MethodInsnNode && insn.owner == companionName) {
            if (insn.opcode == org.objectweb.asm.Opcodes.INVOKEVIRTUAL) {
                val type = org.objectweb.asm.Type.getMethodType(insn.desc)
                insn.opcode = org.objectweb.asm.Opcodes.INVOKESTATIC
                insn.owner = ownerName
                insn.itf = ownerIsInterface
                insn.name = "viaFabricPlus\$body\$" + (insn.name + insn.desc).hashCode().toString().replace("-", "_")
                insn.desc = org.objectweb.asm.Type.getMethodDescriptor(
                    type.returnType,
                    org.objectweb.asm.Type.getObjectType(companionName),
                    *type.argumentTypes
                )
            } else if (insn.opcode == org.objectweb.asm.Opcodes.INVOKESTATIC) {
                insn.owner = ownerName
                insn.itf = ownerIsInterface
            }
        }
        if (insn is org.objectweb.asm.tree.InvokeDynamicInsnNode) {
            insn.bsmArgs.forEachIndexed { index, arg ->
                if (arg is org.objectweb.asm.Handle && arg.owner == companionName) {
                    insn.bsmArgs[index] = org.objectweb.asm.Handle(
                        arg.tag, ownerName, arg.name, arg.desc, arg.isInterface
                    )
                }
            }
        }
    }
}
