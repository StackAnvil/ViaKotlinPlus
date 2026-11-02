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

// Mixin requires static fields declared by a mixin to be private, and static
// shadow fields must not get an initialiser (Mixin would copy it into the
// target). Kotlin cannot express either, so rewrite the compiled mixins.
tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    doLast {
        val classesDir = destinationDirectory.get().asFile
        val asm = org.objectweb.asm.Opcodes.ASM9
        val shadowDesc = "Lorg/spongepowered/asm/mixin/Shadow;"
        classesDir.walkTopDown().filter { it.isFile && it.extension == "class" }.forEach { file ->
            val node = org.objectweb.asm.tree.ClassNode()
            org.objectweb.asm.ClassReader(file.readBytes()).accept(node, 0)
            val mixin = (node.visibleAnnotations?.any { it.desc == "Lorg/spongepowered/asm/mixin/Mixin;" } == true) ||
                (node.invisibleAnnotations?.any { it.desc == "Lorg/spongepowered/asm/mixin/Mixin;" } == true)
            if (!mixin) return@forEach
            val isInterface = node.access and org.objectweb.asm.Opcodes.ACC_INTERFACE != 0
            val shadows = node.fields.filter { f ->
                (f.visibleAnnotations?.any { it.desc == shadowDesc } == true) ||
                    (f.invisibleAnnotations?.any { it.desc == shadowDesc } == true)
            }.map { it.name }.toSet()
            if (isInterface) {
                node.fields.clear()
                node.methods.removeIf { it.name == "<clinit>" }
                node.methods.forEach { m ->
                    if (m.access and org.objectweb.asm.Opcodes.ACC_STATIC != 0) {
                        m.access = (m.access and org.objectweb.asm.Opcodes.ACC_PUBLIC.inv()
                            and org.objectweb.asm.Opcodes.ACC_PROTECTED.inv()
                            and org.objectweb.asm.Opcodes.ACC_PRIVATE.inv()) or org.objectweb.asm.Opcodes.ACC_PRIVATE
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
                method.instructions.forEach { insn ->
                    if (insn is org.objectweb.asm.tree.FieldInsnNode &&
                        insn.opcode == org.objectweb.asm.Opcodes.GETSTATIC &&
                        insn.owner == node.name && insn.name == "Companion"
                    ) {
                        insn.owner = node.name + "\$Companion"
                        insn.name = "\$\$INSTANCE"
                    }
                }
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
                // Kotlin emits NOPs in constructors; Mixin's initialiser parser
                // expects the field store immediately after the value.
                if (method.name == "<init>" || method.name == "<clinit>") {
                    val it = method.instructions.iterator()
                    while (it.hasNext()) {
                        if (it.next().opcode == org.objectweb.asm.Opcodes.NOP) it.remove()
                    }
                }
            }
            val writer = org.objectweb.asm.ClassWriter(0)
            node.accept(writer)
            file.writeBytes(writer.toByteArray())
        }
    }
}
