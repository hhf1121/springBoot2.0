package com.hhf.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseConfigExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public BaseConfigExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andConfigNameIsNull() {
            addCriterion("config_name is null");
            return (Criteria) this;
        }

        public Criteria andConfigNameIsNotNull() {
            addCriterion("config_name is not null");
            return (Criteria) this;
        }

        public Criteria andConfigNameEqualTo(String value) {
            addCriterion("config_name =", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotEqualTo(String value) {
            addCriterion("config_name <>", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameGreaterThan(String value) {
            addCriterion("config_name >", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameGreaterThanOrEqualTo(String value) {
            addCriterion("config_name >=", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameLessThan(String value) {
            addCriterion("config_name <", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameLessThanOrEqualTo(String value) {
            addCriterion("config_name <=", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameLike(String value) {
            addCriterion("config_name like", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotLike(String value) {
            addCriterion("config_name not like", value, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameIn(List<String> values) {
            addCriterion("config_name in", values, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotIn(List<String> values) {
            addCriterion("config_name not in", values, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameBetween(String value1, String value2) {
            addCriterion("config_name between", value1, value2, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigNameNotBetween(String value1, String value2) {
            addCriterion("config_name not between", value1, value2, "configName");
            return (Criteria) this;
        }

        public Criteria andConfigCodeIsNull() {
            addCriterion("config_code is null");
            return (Criteria) this;
        }

        public Criteria andConfigCodeIsNotNull() {
            addCriterion("config_code is not null");
            return (Criteria) this;
        }

        public Criteria andConfigCodeEqualTo(String value) {
            addCriterion("config_code =", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeNotEqualTo(String value) {
            addCriterion("config_code <>", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeGreaterThan(String value) {
            addCriterion("config_code >", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeGreaterThanOrEqualTo(String value) {
            addCriterion("config_code >=", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeLessThan(String value) {
            addCriterion("config_code <", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeLessThanOrEqualTo(String value) {
            addCriterion("config_code <=", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeLike(String value) {
            addCriterion("config_code like", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeNotLike(String value) {
            addCriterion("config_code not like", value, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeIn(List<String> values) {
            addCriterion("config_code in", values, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeNotIn(List<String> values) {
            addCriterion("config_code not in", values, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeBetween(String value1, String value2) {
            addCriterion("config_code between", value1, value2, "configCode");
            return (Criteria) this;
        }

        public Criteria andConfigCodeNotBetween(String value1, String value2) {
            addCriterion("config_code not between", value1, value2, "configCode");
            return (Criteria) this;
        }

        public Criteria andTypeLabelIsNull() {
            addCriterion("type_label is null");
            return (Criteria) this;
        }

        public Criteria andTypeLabelIsNotNull() {
            addCriterion("type_label is not null");
            return (Criteria) this;
        }

        public Criteria andTypeLabelEqualTo(String value) {
            addCriterion("type_label =", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelNotEqualTo(String value) {
            addCriterion("type_label <>", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelGreaterThan(String value) {
            addCriterion("type_label >", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelGreaterThanOrEqualTo(String value) {
            addCriterion("type_label >=", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelLessThan(String value) {
            addCriterion("type_label <", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelLessThanOrEqualTo(String value) {
            addCriterion("type_label <=", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelLike(String value) {
            addCriterion("type_label like", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelNotLike(String value) {
            addCriterion("type_label not like", value, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelIn(List<String> values) {
            addCriterion("type_label in", values, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelNotIn(List<String> values) {
            addCriterion("type_label not in", values, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelBetween(String value1, String value2) {
            addCriterion("type_label between", value1, value2, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeLabelNotBetween(String value1, String value2) {
            addCriterion("type_label not between", value1, value2, "typeLabel");
            return (Criteria) this;
        }

        public Criteria andTypeValueIsNull() {
            addCriterion("type_value is null");
            return (Criteria) this;
        }

        public Criteria andTypeValueIsNotNull() {
            addCriterion("type_value is not null");
            return (Criteria) this;
        }

        public Criteria andTypeValueEqualTo(Integer value) {
            addCriterion("type_value =", value, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueNotEqualTo(Integer value) {
            addCriterion("type_value <>", value, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueGreaterThan(Integer value) {
            addCriterion("type_value >", value, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueGreaterThanOrEqualTo(Integer value) {
            addCriterion("type_value >=", value, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueLessThan(Integer value) {
            addCriterion("type_value <", value, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueLessThanOrEqualTo(Integer value) {
            addCriterion("type_value <=", value, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueIn(List<Integer> values) {
            addCriterion("type_value in", values, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueNotIn(List<Integer> values) {
            addCriterion("type_value not in", values, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueBetween(Integer value1, Integer value2) {
            addCriterion("type_value between", value1, value2, "typeValue");
            return (Criteria) this;
        }

        public Criteria andTypeValueNotBetween(Integer value1, Integer value2) {
            addCriterion("type_value not between", value1, value2, "typeValue");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIsNull() {
            addCriterion("is_delete is null");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIsNotNull() {
            addCriterion("is_delete is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeleteEqualTo(Integer value) {
            addCriterion("is_delete =", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotEqualTo(Integer value) {
            addCriterion("is_delete <>", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteGreaterThan(Integer value) {
            addCriterion("is_delete >", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_delete >=", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLessThan(Integer value) {
            addCriterion("is_delete <", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteLessThanOrEqualTo(Integer value) {
            addCriterion("is_delete <=", value, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteIn(List<Integer> values) {
            addCriterion("is_delete in", values, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotIn(List<Integer> values) {
            addCriterion("is_delete not in", values, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteBetween(Integer value1, Integer value2) {
            addCriterion("is_delete between", value1, value2, "isDelete");
            return (Criteria) this;
        }

        public Criteria andIsDeleteNotBetween(Integer value1, Integer value2) {
            addCriterion("is_delete not between", value1, value2, "isDelete");
            return (Criteria) this;
        }

        public Criteria andLastTimeIsNull() {
            addCriterion("last_time is null");
            return (Criteria) this;
        }

        public Criteria andLastTimeIsNotNull() {
            addCriterion("last_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastTimeEqualTo(Date value) {
            addCriterion("last_time =", value, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeNotEqualTo(Date value) {
            addCriterion("last_time <>", value, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeGreaterThan(Date value) {
            addCriterion("last_time >", value, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_time >=", value, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeLessThan(Date value) {
            addCriterion("last_time <", value, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_time <=", value, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeIn(List<Date> values) {
            addCriterion("last_time in", values, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeNotIn(List<Date> values) {
            addCriterion("last_time not in", values, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeBetween(Date value1, Date value2) {
            addCriterion("last_time between", value1, value2, "lastTime");
            return (Criteria) this;
        }

        public Criteria andLastTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_time not between", value1, value2, "lastTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}