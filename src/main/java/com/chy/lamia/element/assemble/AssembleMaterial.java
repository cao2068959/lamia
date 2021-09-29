package com.chy.lamia.element.assemble;

import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.entity.Expression;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.ParameterTypeMemberAnnotation;

import java.util.Optional;

import static com.chy.lamia.constant.PriorityConstant.*;

public class AssembleMaterial {

    ParameterType parameterType;
    Expression expression;
    Integer priority;
    //如果是一个对象中的字段,那么就会存在对应的 parent
    Optional<AssembleMaterial> parent = Optional.empty();
    Optional<MapMember> mapMember = Optional.empty();
    AssembleMaterialSource source;
    Boolean spread;


    public AssembleMaterial(ParameterType parameterType, Expression expression, AssembleMaterialSource source) {
        this.parameterType = parameterType;
        this.expression = expression;
        this.source = source;
        this.priority = COMMON;
    }

    public AssembleMaterial(ParameterTypeMemberAnnotation parameterType, Expression expression, AssembleMaterialSource source) {
        this((ParameterType) parameterType, expression, source);
        Integer priority = parameterType.getMapMember().map(MapMember::priority).orElse(-1);
        if (priority >= 0) {
            this.priority = priority;
        }
    }


    public AssembleMaterial(ParameterType parameterType, Expression expression) {
        this.parameterType = parameterType;
        this.expression = expression;
        this.source = AssembleMaterialSource.OTHER;
        this.priority = OTHER;
    }

    public boolean isSpread() {
        if (spread != null) {
            return spread;
        }

        spread = doIsSpread();
        return spread;
    }

    private boolean doIsSpread() {
        return this.getMapMember().map(MapMember::spread)
                .orElseGet(() -> {
                    if (this.getSource() == AssembleMaterialSource.PARAMETER) {
                        return true;
                    }
                    return false;
                });
    }


    /**
     * 获取最顶层的 parent
     *
     * @return
     */
    public Optional<AssembleMaterial> getTopParent() {
        if (!parent.isPresent()) {
            return parent;
        }
        Optional<AssembleMaterial> result = parent;
        while (true) {
            Optional<AssembleMaterial> newParent = result.get().getParent();
            //到头了
            if (!newParent.isPresent()) {
                return result;
            }
            result = newParent;
        }
    }


    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public Expression getExpression() {
        return expression;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Optional<AssembleMaterial> getParent() {
        return parent;
    }

    public void setParent(AssembleMaterial parent) {
        this.parent = Optional.of(parent);
    }

    public boolean hasParent() {
        return parent.isPresent();
    }

    public Optional<MapMember> getMapMember() {
        return mapMember;
    }

    public void setMapMember(MapMember mapMember) {
        this.mapMember = Optional.of(mapMember);
    }

    public void setMapMember(Optional<MapMember> mapMember) {
        this.mapMember = mapMember;
    }

    public AssembleMaterialSource getSource() {
        return source;
    }


}
