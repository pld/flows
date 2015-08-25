package io.rapidpro.flows.definition.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.rapidpro.expressions.EvaluatedTemplate;
import io.rapidpro.expressions.EvaluationContext;
import io.rapidpro.flows.Flows;
import io.rapidpro.flows.definition.Flow;
import io.rapidpro.flows.definition.FlowParseException;
import io.rapidpro.flows.runner.Input;
import io.rapidpro.flows.runner.RunState;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds the contact to one or more groups
 */
public class AddToGroupAction extends Action {

    @SerializedName("groups")
    protected List<String> m_groups;

    public AddToGroupAction(List<String> groups) {
        m_groups = groups;
    }

    /**
     * @see Action#fromJson(JsonObject, Flow.DeserializationContext)
     */
    public static AddToGroupAction fromJson(JsonObject json, Flow.DeserializationContext context) throws FlowParseException {
        List<String> groups = new ArrayList<>();
        for (JsonElement groupElem : json.get("groups").getAsJsonArray()) {
            groups.add(groupElem.getAsJsonObject().get("name").getAsString());
        }
        return new AddToGroupAction(groups);
    }

    /**
     * @see Action#execute(Flows.Runner, RunState, Input)
     */
    @Override
    public Result execute(Flows.Runner runner, RunState run, Input input) {
        EvaluationContext context = run.buildContext(input);
        List<String> groups = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String group : m_groups) {
            EvaluatedTemplate template = run.substituteVariables(group, context);

            if (!template.hasErrors()) {
                run.getContact().getGroups().add(template.getOutput());
                groups.add(template.getOutput());
            } else {
                errors.add(group);
            }
        }

        Action performed = groups.size() > 0 ? new AddToGroupAction(groups) : null;
        return new Result(performed, errors);
    }

    public List<String> getGroups() {
        return m_groups;
    }
}
