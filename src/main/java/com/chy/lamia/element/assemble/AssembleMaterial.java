package com.chy.lamia.element.assemble;

import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

import java.util.Optional;

import static com.chy.lamia.constant.PriorityConstant.*;

public class AssembleMaterial {

    ParameterType parameterType;
    JCTree.JCExpression expression;
    Integer priority;

    Optional<AssembleMaterial> parent = Optional.empty();
    Optional<MapMember> mapMember = Optional.empty();
    AssembleMaterialSource source;

    public AssembleMaterial(ParameterType parameterType, JCTree.JCExpression expression, AssembleMaterialSource source) {
        this.parameterType = parameterType;
        this.expression = expression;

        if (source == AssembleMaterialSource.PARAMETER) {
            this.priority = PARAMETERS;
        } else if (source == AssembleMaterialSource.METHOD_VAR) {
            this.priority = METHOD_BODY_VAR;
        } else {
            this.priority = OTHER;
        }
    }

    public AssembleMaterial(ParameterType parameterType, JCTree.JCExpression expression) {
        this.parameterType = parameterType;
        this.expression = expression;
        this.source = AssembleMaterialSource.OTHER;
        this.priority = OTHER;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }

    public void setExpression(JCTree.JCExpression expression) {
        this.expression = expression;
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
