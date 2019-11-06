package cn.poile.blog.aspect;

import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author: yaohw
 * @create: 2019-11-06 18:26
 **/
public class RateLimiterAspectExpressContext {

    private final EvaluationContext context;
    private final ExpressionParser parser;
    private final ParserContext parserContext;

    private RateLimiterAspectExpressContext() {
        this(null);
    }

    private RateLimiterAspectExpressContext(ApplicationContext applicationContext) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (applicationContext != null) {
            context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        }
        SpelParserConfiguration config = new SpelParserConfiguration(true, true);
        this.context = context;
        this.parser = new SpelExpressionParser(config);
        this.parserContext = new TemplateParserContext();
    }

    public RateLimiterAspectExpressContext(Object target, Object[] args, Object result) {
        this();
        context.setVariable("target", target);
        context.setVariable("result", result);
        for (int i = 0; i < args.length; i++) {
            context.setVariable("a" + i, args[i]);
        }
    }

    public String getValue(String express) {
        Expression expression = parser.parseExpression(express, parserContext);
        return expression.getValue(context, String.class);
    }
}
