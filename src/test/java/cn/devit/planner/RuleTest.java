package cn.devit.planner;

import static org.junit.Assert.*;

import java.net.URL;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;

public class RuleTest {

    @Test
    public void test1() throws Exception {

        URL resource = getClass().getResource("debug.drl");
        System.out.println(resource);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("debug.drl", getClass()),
                ResourceType.DRL);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        KieSession kSession = kbase.newKieSession();
        kSession.insert(new Leg("PEK", "SHA"));
        kSession.fireAllRules();
    }
}
