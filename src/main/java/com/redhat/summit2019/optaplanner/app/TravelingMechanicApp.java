/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.redhat.summit2019.optaplanner.app;

import com.redhat.summit2019.optaplanner.domain.TravelingMechanicSolution;
import com.redhat.summit2019.optaplanner.swingui.TravelingMechanicPanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class TravelingMechanicApp extends CommonApp<TravelingMechanicSolution> {

    public static final String SOLVER_CONFIG
            = "com/redhat/summit2019/optaplanner/solver/travelingMechanicSolverConfig.xml";

    public static final String DATA_DIR_NAME = "travelingmechanic";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TravelingMechanicApp().init();
    }

    public TravelingMechanicApp() {
        super("Traveling Mechanic",
                "",
                SOLVER_CONFIG, DATA_DIR_NAME,
                null);
    }

    @Override
    protected TravelingMechanicPanel createSolutionPanel() {
        return new TravelingMechanicPanel();
    }

    @Override
    public SolutionFileIO<TravelingMechanicSolution> createSolutionFileIO() {
        return new XStreamSolutionFileIO<>(TravelingMechanicSolution.class);
    }

}
