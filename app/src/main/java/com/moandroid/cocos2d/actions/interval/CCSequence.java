package com.moandroid.cocos2d.actions.interval;

import java.util.ArrayList;

import com.moandroid.cocos2d.actions.CCFiniteTimeAction;
import com.moandroid.cocos2d.nodes.CCNode;

public class CCSequence extends CCIntervalAction {

	private ArrayList<CCFiniteTimeAction> actions;
	private float split;
	private int last;//标记上一次执行动画的位置


	public static CCFiniteTimeAction actions(CCFiniteTimeAction action1, CCFiniteTimeAction... actions) {
		CCFiniteTimeAction prev = action1;
		for (CCFiniteTimeAction now : actions) {
			prev = new CCSequence(prev, now);
		}
		return (CCFiniteTimeAction) prev;
	}

	protected CCSequence(CCFiniteTimeAction one, CCFiniteTimeAction two) {
		//assert one != null : "Sequence: argument one must be non-null";
		//assert two != null : "Sequence: argument two must be non-null";

		super(one.duration() + two.duration());

		actions = new ArrayList<CCFiniteTimeAction>(2);
		actions.add(one);
		actions.add(two);
	}

	@Override
	public CCSequence copy() {
		return new CCSequence(actions.get(0).copy(), actions.get(1).copy());
	}

	@Override
	public void start(CCNode aTarget) {
		super.start(aTarget);
		split = actions.get(0).duration() / _duration;//两个动画的百分比分割时间点
		last = -1;
	}

	public void stop() {
		for (CCFiniteTimeAction action : actions)
			action.stop();
		super.stop();
	}

	@Override
	public void update(float t) {
		int found;
		float new_t;
		if (t >= split) {//如果sequence第一个动画执行完成
			found = 1;//标记为下一个为动画
			if (split == 1)
				new_t = 1;
			else
				new_t = (t - split) / (1 - split);// new_t 计算多出时间在下一个时间动画所占百分比
		} else {
			found = 0;//还是在第一个动画中执行
			if (split != 0)
				new_t = t / split;// 第一个动画中过去时间说占用的百分比
			else
				new_t = 1;
		}

		if (last == -1 && found == 1) {// 如果第一个动画的时间已过去（可能是卡帧什么导致）
			actions.get(0).start(_target);
			actions.get(0).update(1.0f);//直接执行第一个动画的末尾处并停止
			actions.get(0).stop();
		}

		if (last != found) {// 如果上一次动画的执行时间已过去
			if (last != -1) {
				actions.get(last).update(1.0f);//执行完成并结束
				actions.get(last).stop();
			}
			actions.get(found).start(_target);
		}
		actions.get(found).update(new_t);
		last = found;
	}

	@Override
	public CCSequence reverse() {
		return new CCSequence(actions.get(1).reverse(), actions.get(0).reverse());
	}

}
