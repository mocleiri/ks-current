/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.student.lum.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.TimeAmountInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.lum.course.dto.ActivityInfo;
import org.kuali.student.r2.lum.course.dto.CourseInfo;
import org.kuali.student.r2.lum.course.dto.FormatInfo;
import org.kuali.student.r2.lum.lrc.dto.ResultValueRangeInfo;
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;
import org.kuali.student.service.remote.impl.CourseServiceRemoteImpl;

/**
 *
 * @author nwright
 */
@Ignore
public class CoursePostProcessorBaseTest {

    public CoursePostProcessorBaseTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private static final String LOCAL_HOST_URL = "http://localhost:8080/ks-cm";

    @Test
    public void testCopyCourseOnTopOfAnother() throws Exception {
        try {
            System.out.println("testCopyCourseOnTopOfAnother");
            CourseServiceRemoteImpl courseService = new CourseServiceRemoteImpl();
            courseService.setHostUrl(LOCAL_HOST_URL);
            ContextInfo contextInfo = new ContextInfo();
            contextInfo.setPrincipalId("admin");

            CourseInfo original = new CourseInfo();
            original.setCourseTitle("Test Course");
            original.setTranscriptTitle("Test Course");
            original.setSubjectArea("ENGL");
            original.setCourseNumberSuffix("101");
            original.setCode("ENGL101");
            original.setTypeKey(CluServiceConstants.CREDIT_COURSE_LU_TYPE_KEY);
            original.setStateKey("Active");
            original.setUnitsContentOwner(Arrays.asList("1000100"));
            original.setCampusLocations(Arrays.asList("NO"));
            original.setGradingOptions(Arrays.asList("kuali.resultComponent.grade.passFail"));
            TimeAmountInfo duration = new TimeAmountInfo();
            duration.setAtpDurationTypeKey("kuali.atp.duration.Semester");
            duration.setTimeQuantity(1);
            original.setDuration(duration);

            ResultValuesGroupInfo rvg = new ResultValuesGroupInfo();
            List<AttributeInfo> rciAttributes = new ArrayList<AttributeInfo>();
            AttributeInfo attInfo = new AttributeInfo();
            attInfo.setKey("fixedCreditValue");
            attInfo.setValue("3");
            rciAttributes.add(attInfo);
            //rci.setTypeKey("kuali.resultComponentType.credit.degree.fixed");
            rvg.setTypeKey("kuali.result.values.group.type.fixed");
            ResultValueRangeInfo resutlValueRangeInfo = new ResultValueRangeInfo();
            resutlValueRangeInfo.setMinValue("3");
            resutlValueRangeInfo.setMaxValue("3");
            rvg.setResultValueRange(resutlValueRangeInfo);
            original.setCreditOptions(Arrays.asList(rvg));
            AttributeInfo finalExam = new AttributeInfo();
            finalExam.setKey("finalExamStatus");
            finalExam.setValue("STD");
            original.getAttributes().add(finalExam);
            original.setDescr(new RichTextHelper().fromPlain("this is my description"));
            original.setStartTerm("20144");
            original.setTermsOffered(Arrays.asList("kuali.season.Fall"));
            
            FormatInfo format = new FormatInfo ();
            format.setTypeKey(CluServiceConstants.COURSE_FORMAT_TYPE_KEY);
            format.setStateKey("Active");
            format.setDuration(new TimeAmountInfo ());
            format.getDuration().setAtpDurationTypeKey("kuali.atp.duration.Week");
            format.getDuration().setTimeQuantity(2);
            original.getFormats().add(format);
            ActivityInfo activity = new ActivityInfo ();
            activity.setTypeKey(CluServiceConstants.COURSE_ACTIVITY_LECTURE_TYPE_KEY);
            activity.setStateKey("Active");
            format.getActivities().add(activity);
            

            CourseInfo created = courseService.createCourse(original, contextInfo);
            assertEquals("Test Course", created.getCourseTitle());          
            assertEquals (1, created.getFormats().size());
            
            
            CourseInfo gotten = courseService.getCourse(created.getId(), contextInfo);
            assertEquals("Test Course", gotten.getCourseTitle());         
            assertEquals (1, gotten.getFormats().size());
            assertEquals ("kuali.atp.duration.Week", gotten.getFormats().get(0).getDuration().getAtpDurationTypeKey());
            assertEquals (new Integer (2), gotten.getFormats().get(0).getDuration().getTimeQuantity());        
            assertEquals (1, gotten.getFormats().get(0).getActivities().size());
            assertEquals (CluServiceConstants.COURSE_ACTIVITY_LECTURE_TYPE_KEY,
                    gotten.getFormats().get(0).getActivities().get(0).getTypeKey());       

            CourseInfo newVersion = courseService.createNewCourseVersion(gotten.getVersion().getVersionIndId(), "test copying",
                    contextInfo);
            // reset the start and end terms just like we had to do in the data service because the stupid createNewCourseVersion 
            // blanks them out!!!!!
            newVersion.setStartTerm(gotten.getStartTerm());
            newVersion.setEndTerm(gotten.getEndTerm());
            newVersion = courseService.updateCourse(newVersion.getId(), newVersion, contextInfo);
            assertEquals("Test Course", newVersion.getCourseTitle());         
            assertEquals (1, newVersion.getFormats().size());
            assertEquals ("kuali.atp.duration.Week", newVersion.getFormats().get(0).getDuration().getAtpDurationTypeKey());
            assertEquals (new Integer (2), newVersion.getFormats().get(0).getDuration().getTimeQuantity());     
            assertEquals (1, newVersion.getFormats().get(0).getActivities().size());
            assertEquals (CluServiceConstants.COURSE_ACTIVITY_LECTURE_TYPE_KEY,
                    newVersion.getFormats().get(0).getActivities().get(0).getTypeKey());       
            
            newVersion.setCourseTitle(original.getCourseTitle()+ " X");
            newVersion.getFormats().get(0).getDuration().setAtpDurationTypeKey("kuali.atp.duration.Month");
            newVersion.getFormats().get(0).getDuration().setTimeQuantity(3);            
            newVersion.getFormats().get(0).getActivities().clear();
            activity = new ActivityInfo ();
            activity.setTypeKey(CluServiceConstants.COURSE_ACTIVITY_LAB_TYPE_KEY);
            activity.setStateKey("Active");
            newVersion.getFormats().get(0).getActivities().add(activity);
            
            assertEquals("Test Course X", newVersion.getCourseTitle());         
            assertEquals (1, newVersion.getFormats().size());
            assertEquals ("kuali.atp.duration.Month", newVersion.getFormats().get(0).getDuration().getAtpDurationTypeKey());
            assertEquals (new Integer (3), newVersion.getFormats().get(0).getDuration().getTimeQuantity());       
            assertEquals (1, newVersion.getFormats().get(0).getActivities().size());
            assertEquals (CluServiceConstants.COURSE_ACTIVITY_LAB_TYPE_KEY,
                    newVersion.getFormats().get(0).getActivities().get(0).getTypeKey());       


            CourseInfo updatedNewVersion = courseService.updateCourse(newVersion.getId(), newVersion, contextInfo);
            assertEquals("Test Course X", updatedNewVersion.getCourseTitle());         
            assertEquals (1, updatedNewVersion.getFormats().size());
            assertEquals ("kuali.atp.duration.Month", updatedNewVersion.getFormats().get(0).getDuration().getAtpDurationTypeKey());
            assertEquals (new Integer (3), updatedNewVersion.getFormats().get(0).getDuration().getTimeQuantity());     
            assertEquals (1, updatedNewVersion.getFormats().get(0).getActivities().size());
            assertEquals (CluServiceConstants.COURSE_ACTIVITY_LAB_TYPE_KEY,
                    updatedNewVersion.getFormats().get(0).getActivities().get(0).getTypeKey());         

            new CoursePostProcessorBase().copyToCurrentVersion(gotten, updatedNewVersion);
            assertEquals("Test Course X", gotten.getCourseTitle());         
            assertEquals (1, gotten.getFormats().size());
            assertEquals ("kuali.atp.duration.Month", gotten.getFormats().get(0).getDuration().getAtpDurationTypeKey());
            assertEquals (new Integer (3), gotten.getFormats().get(0).getDuration().getTimeQuantity());     
            assertEquals (1, gotten.getFormats().get(0).getActivities().size());
            assertEquals (CluServiceConstants.COURSE_ACTIVITY_LAB_TYPE_KEY,
                    gotten.getFormats().get(0).getActivities().get(0).getTypeKey());         

            CourseInfo originalUpdated = courseService.updateCourse(gotten.getId(), gotten, contextInfo);
            assertEquals("Test Course X", originalUpdated.getCourseTitle());            
            assertEquals (1, originalUpdated.getFormats().size());
            assertEquals ("kuali.atp.duration.Month", originalUpdated.getFormats().get(0).getDuration().getAtpDurationTypeKey());
            assertEquals (new Integer (3), originalUpdated.getFormats().get(0).getDuration().getTimeQuantity());   
            assertEquals (1, originalUpdated.getFormats().get(0).getActivities().size());
            assertEquals (CluServiceConstants.COURSE_ACTIVITY_LAB_TYPE_KEY,
                    originalUpdated.getFormats().get(0).getActivities().get(0).getTypeKey());         
            
        } catch (DataValidationErrorException ex) {
            StringBuilder sb = new StringBuilder();
            String comma = "";
            for (ValidationResultInfo vr : ex.getValidationResults()) {
                sb.append(comma);
                comma = "\n";
                sb.append(vr.getElement()).append(":").append(vr.getMessage());
            }
            throw new RuntimeException(sb.toString(), ex);
        }

    }

}
