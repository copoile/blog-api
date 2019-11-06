package cn.poile.blog.common.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author: yaohw
 * @create: 2019-11-06 19:57
 **/
public class SpelParser {
    private static ExpressionParser parser = new SpelExpressionParser();

    public static String getKey(String key, String condition, String[] paramNames, Object[] arguments) {
        try {
            if (!checkCondition(condition, paramNames, arguments)) {
                return null;
            }
            Expression expression = parser.parseExpression(key);
            EvaluationContext context = new StandardEvaluationContext();
            int length = paramNames.length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    context.setVariable(paramNames[i], arguments[i]);
                }
            }
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkCondition(String condition, String[] paramNames, Object[] arguments) {
        if (condition.length() < 1) {
            return true;
        }
        Expression expression = parser.parseExpression(condition);
        EvaluationContext context = new StandardEvaluationContext();
        int length = paramNames.length;
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                context.setVariable(paramNames[i], arguments[i]);
            }
        }
        return expression.getValue(context, boolean.class);
    }

    public static void main(String[] args) {
        String key = "'getAudioListByBIdNo.'+#bIdNo";
        System.out.println(getKey(key, "", new String[]{"bIdNo"}, new Object[]{"B19001084"}));
    }
}
