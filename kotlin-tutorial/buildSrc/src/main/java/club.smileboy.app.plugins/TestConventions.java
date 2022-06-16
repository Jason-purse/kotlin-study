package club.smileboy.app.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencyResolveDetails;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.testing.Test;

/**
 * @author JASONJ
 * @date 2022/6/15
 * @time 21:59
 * @description 进行测试约定 ....
 **/
public class TestConventions implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,"org.junit.jupiter:junit-jupiter");
        project.getPlugins().withType(JavaPlugin.class, (javaPlugin) -> project.getDependencies()
                .add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.platform:junit-platform-launcher"));
        // 添加测试任务 ...
        project.getConfigurations()
                .all( files -> files.getResolutionStrategy().eachDependency(dependencyResolveDetails -> {
                    // 如果匹配测试 ...
                    if (dependencyResolveDetails.getRequested().getGroup().equals("org.junit.jupiter")) {
                        // 上面配置的是configuration ,最终 都会转变为类路径 ...
                        dependencyResolveDetails.useVersion("5.6.0");
                        dependencyResolveDetails.because("统一测试框架版本为 5.6 ...");
                    }
                }));


        project.getTasks().named("test", Test.class, test -> {
            test.useJUnitPlatform();
            test.useJUnit();
            test.useTestNG();
            test.filter(testFilter -> {
                testFilter.includeTestsMatching("*Test");
            });
        });

    }
}
