package io.rapidpro.flows.definition.tests.location;

import com.google.gson.JsonObject;
import io.rapidpro.expressions.EvaluationContext;
import io.rapidpro.flows.definition.Flow;
import io.rapidpro.flows.definition.FlowParseException;
import io.rapidpro.flows.definition.tests.Test;
import io.rapidpro.flows.runner.Location;
import io.rapidpro.flows.runner.RunState;
import io.rapidpro.flows.runner.Runner;
import org.apache.commons.lang3.StringUtils;

/**
 * Test that returns whether the text contains the valid state
 */
public class HasStateTest extends Test {

    /**
     * @see Test#fromJson(JsonObject, Flow.DeserializationContext)
     */
    public static HasStateTest fromJson(JsonObject obj, Flow.DeserializationContext context) throws FlowParseException {
        return new HasStateTest();
    }

    /**
     * @see Test#evaluate(Runner, RunState, EvaluationContext, String)
     */
    @Override
    public Result evaluate(Runner runner, RunState run, EvaluationContext context, String text) {
        String country = run.getOrg().getCountry();
        if (StringUtils.isNotEmpty(country)) {
            Location location = runner.getLocationResolver().resolve(text, country, Location.Level.STATE, null);
            if (location != null) {
                return Result.textMatch(location.getName());
            }
        }

        return Result.NO_MATCH;
    }
}