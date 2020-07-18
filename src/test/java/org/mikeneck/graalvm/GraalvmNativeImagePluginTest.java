/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.mikeneck.graalvm;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * A simple unit test for the 'org.mikeneck.graalvm.greeting' plugin.
 */
public class GraalvmNativeImagePluginTest {

    @Test public void pluginRegistersNativeImageTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("java");

        project.getPlugins().apply("org.mikeneck.graalvm-native-image");

        Task nativeImageTask = project.getTasks().getByName("nativeImage");
        assertNotNull(nativeImageTask);
        assertThat(nativeImageTask, instanceOf(DefaultNativeImageTask.class));
    }

    @Test
    public void generateNativeImageConfigTasksAreDisabled() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("java");
        project.getPlugins().apply("org.mikeneck.graalvm-native-image");

        TaskCollection<GenerateNativeImageConfigTask> generateNativeImageConfigTasks =
                project.getTasks().withType(GenerateNativeImageConfigTask.class);
        assertThat(generateNativeImageConfigTasks)
                .describedAs("generateNativeTasks")
                .hasSize(1)
                .allSatisfy(task -> assertThat(task.getEnabled()).isFalse());

        TaskCollection<MergeNativeImageConfigTask> mergeNativeImageConfigTasks =
                project.getTasks().withType(MergeNativeImageConfigTask.class);
        assertThat(mergeNativeImageConfigTasks)
                .describedAs("mergeNativeImageConfigTasks")
                .hasSize(1)
                .allSatisfy(task -> assertThat(task.getEnabled()).isFalse());
    }
}
