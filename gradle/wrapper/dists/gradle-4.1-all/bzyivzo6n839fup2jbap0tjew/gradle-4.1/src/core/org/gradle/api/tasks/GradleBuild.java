/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.tasks;

import org.gradle.StartParameter;
import org.gradle.api.internal.ConventionTask;
import org.gradle.initialization.NestedBuildFactory;
import org.gradle.internal.invocation.BuildController;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Executes a Gradle build.
 */
public class GradleBuild extends ConventionTask {
    private final NestedBuildFactory nestedBuildFactory;
    private StartParameter startParameter;

    public GradleBuild() {
        this.nestedBuildFactory = getServices().get(NestedBuildFactory.class);
        this.startParameter = getServices().get(StartParameter.class).newBuild();
        startParameter.setCurrentDir(getProject().getProjectDir());
    }

    /**
     * Returns the full set of parameters that will be used to execute the build.
     *
     * @return the parameters. Never returns null.
     */
    @Internal
    public StartParameter getStartParameter() {
        return startParameter;
    }

    /**
     * Sets the full set of parameters that will be used to execute the build.
     *
     * @param startParameter the parameters. Should not be null.
     */
    public void setStartParameter(StartParameter startParameter) {
        this.startParameter = startParameter;
    }

    /**
     * Returns the project directory for the build. Defaults to the project directory.
     *
     * @return The project directory. Never returns null.
     */
    @Internal
    public File getDir() {
        return getStartParameter().getCurrentDir();
    }

    /**
     * Sets the project directory for the build.
     *
     * @param dir The project directory. Should not be null.
     * @since 4.0
     */
    public void setDir(File dir) {
        setDir((Object) dir);
    }

    /**
     * Sets the project directory for the build.
     *
     * @param dir The project directory. Should not be null.
     */
    public void setDir(Object dir) {
        getStartParameter().setCurrentDir(getProject().file(dir));
    }

    /**
     * Returns the build file that should be used for this build. Defaults to {@value
     * org.gradle.api.Project#DEFAULT_BUILD_FILE} in the project directory.
     *
     * @return The build file. May be null.
     */
    @Optional @InputFile
    public File getBuildFile() {
        return getStartParameter().getBuildFile();
    }

    /**
     * Sets the build file that should be used for this build.
     *
     * @param file The build file. May be null to use the default build file for the build.
     * @since 4.0
     */
    public void setBuildFile(File file) {
        setBuildFile((Object) file);
    }

    /**
     * Sets the build file that should be used for this build.
     *
     * @param file The build file. May be null to use the default build file for the build.
     */
    public void setBuildFile(Object file) {
        getStartParameter().setBuildFile(getProject().file(file));
    }

    /**
     * Returns the tasks that should be executed for this build.
     *
     * @return The sequence. May be empty. Never returns null.
     */
    @Input
    public List<String> getTasks() {
        return getStartParameter().getTaskNames();
    }

    /**
     * Sets the tasks that should be executed for this build.
     *
     * @param tasks The task names. May be empty or null to use the default tasks for the build.
     * @since 4.0
     */
    public void setTasks(List<String> tasks) {
        setTasks((Collection<String>) tasks);
    }

    /**
     * Sets the tasks that should be executed for this build.
     *
     * @param tasks The task names. May be empty or null to use the default tasks for the build.
     */
    public void setTasks(Collection<String> tasks) {
        getStartParameter().setTaskNames(tasks);
    }

    @TaskAction
    void build() {
        BuildController buildController = nestedBuildFactory.nestedBuildController(getStartParameter());
        try {
            buildController.run();
        } finally {
            buildController.stop();
        }
    }
}
