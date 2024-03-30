/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.dataflow.analysis;

import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.exp.LValue;
import pascal.taie.ir.exp.RValue;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Stmt;

import java.util.Optional;

/**
 * Implementation of classic live variable analysis.
 */
public class LiveVariableAnalysis extends
        AbstractDataflowAnalysis<Stmt, SetFact<Var>> {

    public static final String ID = "livevar";

    public LiveVariableAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public SetFact<Var> newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        //根据算法，是backward边界节点exit
        //是用来对exit结点初始化，IN[exit] = ∅，初始化为空，new一个空的Var集合返回。
        return new SetFact<>();
    }

    @Override
    public SetFact<Var> newInitialFact() {
        // TODO - finish me
        //用于对其他结点初始化，直接IN[B] = ∅，初始化为空，new一个空的Var集合返回。
        return new SetFact<>();
    }

    @Override
    public void meetInto(SetFact<Var> fact, SetFact<Var> target) {
        // TODO - finish me
        //这里的meetInto，就是把fact集合合并入target集合
        // 这里的target是OUT[B]，fact是IN[S]，S为B的后继。
        // 做的事情就是把后继的IN集合合并到B的OUT集合，也就是说对target和fact求并集
        target.union(fact);
        //这里SetFact里有两个Set合并的接口，直接调用就行。
    }

    @Override
    public boolean transferNode(Stmt stmt, SetFact<Var> in, SetFact<Var> out) {
        // TODO - finish me
        //这里就是算法里需要判断前后的In是不是发生了变化
        //IN的公式是 IN[B] = useB U (OUT[B] - defB);
        SetFact<Var> newIn = new SetFact<>();
        //这里了解到copy是浅拷贝，所以用set实现深拷贝
        newIn.set(out);

        Optional<LValue> defB = stmt.getDef();
        //Optional 类：如果值存在 isPresent()会返回true，get会返回该对象
        //判断是不是为空，然后LValue不是一直是Var，所以，也要判断，两个与起来
        if(defB.isPresent()){
            if(defB.get() instanceof Var){
                newIn.remove((Var) defB.get());//OUT[B]-DEF[B]
            }
        }
        //use B
        //一样的，判断之后直接添加进去
        for(RValue use : stmt.getUses()){
            if(use instanceof Var)
            {
                newIn.add((Var)use);
            }
        }
        //没改变就终止，改了就赋值，继续迭代
        if(in.equals((newIn))){
            return false;
        }
        else{
            in.set(newIn);
            return true;
        }

    }
}
