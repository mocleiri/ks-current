package com.sigmasys.kuali.ksa.service.brm;

import com.sigmasys.kuali.ksa.model.rule.Rule;
import com.sigmasys.kuali.ksa.model.rule.RuleSet;
import com.sigmasys.kuali.ksa.model.rule.RuleTypeEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

/**
 * This utility should be used to build a String representation of a Drools rule or rule set objects.
 *
 * @author Michael Ivanov
 */
public class DroolsRuleBuilder {

    private static final Pattern whitespacePattern = Pattern.compile("\\s+");
    private static final Pattern commaPattern = Pattern.compile(" ,");

    private DroolsRuleBuilder() {
    }

    private static String removeExtraSpaces(String value) {
        return commaPattern.matcher(whitespacePattern.matcher(value).replaceAll(" ")).replaceAll(",");
    }

    private static String normalize(String dslrText) {
        BufferedReader reader = new BufferedReader(new StringReader(dslrText));
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    builder.append(removeExtraSpaces(line));
                } else {
                    builder.append(line);
                }
                builder.append("\n");
            }
            reader.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
        return builder.toString();
    }

    public static String toString(Rule rule) {
        StringBuilder builder = new StringBuilder("rule ");
        // Adding the rule name
        builder.append("\"").append(rule.getName()).append("\"\n");
        // Adding the rule priority
        if (rule.getPriority() != null) {
            builder.append("salience ").append(rule.getPriority()).append("\n");
        }
        // Adding the rule header
        if (rule.getHeader() != null) {
            builder.append(rule.getHeader()).append("\n");
        }
        RuleTypeEnum ruleType = RuleTypeEnum.valueOf(rule.getType().getName());
        String lhs = rule.getLhs();
        String rhs = rule.getRhs();
        if (ruleType == RuleTypeEnum.DSLR) {
            // We have to normalize DSLR by removing extra spaces and tabs from DSLR statements
            lhs = normalize(lhs);
            rhs = normalize(rhs);
        }
        // Adding LHS
        builder.append("when\n    ").append(lhs).append("\n");
        // Adding RHS
        builder.append("then\n    ").append(rhs).append("\n");
        // Adding the rule "end"
        builder.append("end\n");
        return builder.toString();
    }

    public static String toString(RuleSet ruleSet) {
        StringBuilder builder = new StringBuilder(ruleSet.getHeader() != null ? ruleSet.getHeader() : "");
        builder.append("\n");
        if (ruleSet.getRules() != null) {
            for (Rule rule : ruleSet.getRules()) {
                builder.append("\n").append(toString(rule));
            }
        }
        return builder.toString();
    }

}
