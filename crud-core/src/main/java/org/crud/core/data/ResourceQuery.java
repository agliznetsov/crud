package org.crud.core.data;

import lombok.Data;

@Data
public class ResourceQuery {
    Integer skip;
    Integer max;
    boolean count = false;
    Sort sort;
    Filter filter;

//    public void combineCriteria(Criterion value) {
//        if (getCriterion() != null && value != null) {
//            Criteria res = new Criteria();
//            res.setOperator(Criteria.Operator.AND);
//            res.getCriterions().add(getCriterion());
//            res.getCriterions().add(value);
//            setCriterion(res);
//        } else if (value != null)
//            setCriterion(value);
//    }

}
