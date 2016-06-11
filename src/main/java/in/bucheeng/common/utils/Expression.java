/*
 *  @version     1.0, Jan 24, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class Expression {
    private org.springframework.expression.Expression expression;
    private String                                    expressionText;

    private Expression(String expressionText, org.springframework.expression.Expression expression) {
        this.expressionText = expressionText;
        this.expression = expression;

    }

    public static Expression compile(String expressionText) {
        ExpressionParser parser = new SpelExpressionParser();
        return new Expression(expressionText, parser.parseExpression(expressionText, ParserContext.TEMPLATE_EXPRESSION));
    }

    public <T> T evaluate(Map<String, Object> contextParams, Class<T> retType) {
        return expression.getValue(getContext(contextParams), retType);
    }

    /**
     * @param contextParams
     * @return
     */
    private EvaluationContext getContext(Map<String, Object> contextParams) {
        EvaluationContext context = new StandardEvaluationContext();
        for (Map.Entry<String, Object> eContextParam : contextParams.entrySet()) {
            context.setVariable(eContextParam.getKey(), eContextParam.getValue());
        }
        return context;
    }

    public Object evaluate(Map<String, Object> contextParams) {
        return expression.getValue(getContext(contextParams));
    }

    /**
     * @return the expressionText
     */
    public String getExpressionText() {
        return expressionText;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return expressionText;
    }

    public static void main(String[] args) {
        String a = "#{new java.math.BigDecimal(#sellingPrice) > 25000 and #paymentMethod.toUpperCase().trim() == 'COD'}";
        Expression expression = Expression.compile(a);

        Map<String, Object> contextParams = new HashMap<String, Object>();
        contextParams.put("sellingPrice", 25432);
        contextParams.put("paymentMethod", null);
        System.out.println(expression.evaluate(contextParams));
    }
}
