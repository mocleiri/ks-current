/**
 * Copyright 2013 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * Created by mahtabme on 11/5/13
 */
package org.kuali.student.r2.core.scheduling.infc;

import org.kuali.student.r2.common.infc.IdEntity;

import java.util.List;

/**
 * This class represents a constraints on a group of ScheduleRequests.
 * Other rules, such as minimum X of Y schedule requests must be
 * successfully processed, can be added later as more data bits, after
 * proper analysis has been done.
 *
 * @author Mezba Mahtab
 */
public interface ScheduleRequestGroupConstraint extends IdEntity {

    /**
     * Flag that stores if ALL of the ScheduleRequests in this group have to be
     * successfully processed, and should fail for all even if any one of them fails.
     */
    public Boolean getIsAllRequired();

    /**
     * Gets the specific ScheduleRequests that must be processed successfully for the group
     * to be considered successfully processed. Applies only if getIsAllRequired returns false.
     */
    public List<String> getRequiredScheduleRequestIds();

    /**
     * Flag that stores whether the ScheduleRequests should be scheduled contiguously (one
     * after another, with no break). The ScheduleRequests must have their timeslot information
     * completed, and can be of various durations.
     */
    public Boolean getIsContiguous();

    /**
     * Flag that stores whether the ScheduleRequests MUST be all scheduled in the same room.
     * Each ScheduleRequest CAN have different room preferences - if there is a conflict that
     * implies the same room cannot be assigned, then this is a failure.
     */
    public Boolean getIsSameRoomRequired();

    /**
     * Gets all the ScheduleRequests that are part of this group.
     */
    public List<String> getScheduleRequestIds ();

}
