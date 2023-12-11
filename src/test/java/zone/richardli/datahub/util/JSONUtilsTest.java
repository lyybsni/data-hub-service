package zone.richardli.datahub.util;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import zone.richardli.datahub.model.common.FieldDefinition;
import zone.richardli.datahub.utility.JSONUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class JSONUtilsTest {

    @Data
    private static class Embed {

        private String hello;

    }

    @Data
    private static class Test {

        private String name;

        private List<String> list;

        private int number;

        private Embed embed;

    }

    @Data
    private static class MEmbed {

        private String hhello;

    }

    @Data
    private static class MTest {

        private String nname;

        private MEmbed mEmbed;

    }

    private static Test test;
    private static Gson gson = new Gson();

    static {
        Embed embed = new Embed();
        embed.setHello("hello");

        test = new Test();
        test.setName("test");
        test.setList(Arrays.asList("a", "b", "c"));
        test.setNumber(2);
        test.setEmbed(embed);
    }

    @org.junit.jupiter.api.Test
    void test_serialize() {
        log.info("{}", JSONUtils.parseJSONTree(gson.toJson(test)));
    }

    @org.junit.jupiter.api.Test
    void test_mapping() throws JSONException {
        FieldDefinition ruleName = new FieldDefinition();
        ruleName.setPath("nname");
        ruleName.setInherit("name");

        FieldDefinition ruleEmbed = new FieldDefinition();
        ruleEmbed.setPath("mEmbed.hhello");
        ruleEmbed.setInherit("embed.hello");

        List<FieldDefinition> rules = Arrays.asList(
                ruleEmbed,
                ruleName
        );

        MTest mTest = JSONUtils.constructObject(JSONUtils.parseJSONTree(gson.toJson(test)), rules, MTest.class);
        log.info("{}", mTest);
    }

}
