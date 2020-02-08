package com.ribbonconfig;

import com.hhf.myRibbonRule.TheSameClusterPriorityWithVersionRule;
import com.hhf.myRibbonRule.TulingWeightedRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by smlz on 2019/11/20.
 */
@Configuration
public class GlobalRibbonConfig {

    @Bean
    public IRule theSameClusterPriorityRule() {
        return new TulingWeightedRule();
        //return new TheSameClusterPriorityRule();
//        return new TheSameClusterPriorityWithVersionRule();
    }
}
