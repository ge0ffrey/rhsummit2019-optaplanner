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

package com.redhat.summit2019.optaplanner.domain.solver;

import java.util.Objects;

import com.redhat.summit2019.optaplanner.domain.Visit;
import com.redhat.summit2019.optaplanner.domain.VisitOrMechanic;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

// TODO When this class is added only for TimeWindowedVisit, use TimeWindowedVisit instead of Visit
public class DepartureTimeUpdatingVariableListener implements VariableListener<Visit> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Visit visit) {
        updateDepartureTime(scoreDirector, visit);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Visit visit) {
        updateDepartureTime(scoreDirector, visit);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    protected void updateDepartureTime(ScoreDirector scoreDirector, Visit sourceVisit) {
        VisitOrMechanic previous = sourceVisit.getPrevious();
        Long previousDepartureTimeMillis = (previous == null) ? null
                : previous.getDepartureTimeMillis();
        Visit shadowVisit = sourceVisit;
        Long departureTimeMillis = (previousDepartureTimeMillis == null) ? null
                : previousDepartureTimeMillis + shadowVisit.getTravelTimeMillisFromPrevious() + Visit.SERVICE_TIME_MILLIS;
        while (shadowVisit != null && !Objects.equals(shadowVisit.getDepartureTimeMillis(), departureTimeMillis)) {
            scoreDirector.beforeVariableChanged(shadowVisit, "departureTimeMillis");
            shadowVisit.setDepartureTimeMillis(departureTimeMillis);
            scoreDirector.afterVariableChanged(shadowVisit, "departureTimeMillis");
            shadowVisit = shadowVisit.getNext();
            departureTimeMillis = (departureTimeMillis == null || shadowVisit == null) ? null
                    : departureTimeMillis + shadowVisit.getTravelTimeMillisFromPrevious() + Visit.SERVICE_TIME_MILLIS;
        }
    }

}
