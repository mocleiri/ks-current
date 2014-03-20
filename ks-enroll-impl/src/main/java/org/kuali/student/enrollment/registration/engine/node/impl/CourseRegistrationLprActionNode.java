package org.kuali.student.enrollment.registration.engine.node.impl;

import org.kuali.student.enrollment.registration.engine.dto.RegistrationRequestItemEngineMessage;
import org.kuali.student.enrollment.registration.engine.node.AbstractCourseRegistrationNode;
import org.kuali.student.enrollment.registration.engine.processor.CourseRegistrationLprActionProcessor;

/**
 * This class handles processing of all Lpr actions (drop/swap/update/add)
 */
public class CourseRegistrationLprActionNode extends AbstractCourseRegistrationNode<RegistrationRequestItemEngineMessage, RegistrationRequestItemEngineMessage> {

    private CourseRegistrationLprActionProcessor courseRegistrationLprActionProcessor;

    @Override
    public RegistrationRequestItemEngineMessage process(RegistrationRequestItemEngineMessage message) {
        try {
            return courseRegistrationLprActionProcessor.process(message);
        } catch (Exception e) {
            throw new RuntimeException("Error processing", e);
        }
    }

    public void setCourseRegistrationLprActionProcessor(CourseRegistrationLprActionProcessor courseRegistrationLprActionProcessor) {
        this.courseRegistrationLprActionProcessor = courseRegistrationLprActionProcessor;
    }
}
