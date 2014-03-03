/**
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.student.core.process.evaluator;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.framework.engine.PropositionResult;
import org.kuali.student.common.util.krms.RulesExecutionConstants;

import org.kuali.rice.krms.framework.engine.Proposition;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.process.dto.CheckInfo;
import org.kuali.student.r2.core.process.dto.InstructionInfo;
import org.kuali.student.core.krms.proposition.PropositionFactory;

/**
 * Executes a child process and processes those results
 *
 * @author nwright
 */
public class DirectRuleCheckProposition extends AbstractCheckProposition {

    public DirectRuleCheckProposition(InstructionInfo instruction, CheckInfo check) {
        super(instruction, check);
    }

    @Override
    public PropositionResult evaluate(ExecutionEnvironment environment) {
        ContextInfo contextInfo = environment.resolveTerm(RulesExecutionConstants.CONTEXT_INFO_TERM, this);
        PropositionFactory propositionFactory =
                environment.resolveTerm(RulesExecutionConstants.PROPOSITION_FACTORY_TERM, this);
        Proposition directRuleProp;
        try {
            directRuleProp = propositionFactory.getProposition(check.getRuleId(), contextInfo);
        } catch (Exception ex) {
            return KRMSEvaluator.constructExceptionPropositionResult(environment, ex, this);
        }

        PropositionResult directRuleResult = directRuleProp.evaluate(environment);
        if (directRuleResult.getResult()) {
            return this.recordSuccessResult(environment);
        }
        return this.recordFailureResultOrExemption(environment);
    }
}
