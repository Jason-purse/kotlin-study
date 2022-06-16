package club.smileboy.app.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.List;

/**
 * @author JASONJ
 * @date 2022/6/15
 * @time 23:24
 * @description 项目约定 ...
 **/
public class ProjectConventions implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // 默认设置项目的默认任务就是build
        project.setDefaultTasks(List.of("build"));
    }
}
