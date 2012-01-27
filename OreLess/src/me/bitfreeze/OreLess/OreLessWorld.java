package me.bitfreeze.OreLess;

import java.util.ArrayList;

public class OreLessWorld {
	ArrayList<OreLessRule> rules = new ArrayList<OreLessRule>();

	OreLessWorld() {
		rules.clear();
	}

	boolean addRule(OreLessRule rule) {
		rules.add(rule);
		return true;
	}

	boolean removeRule(OreLessRule rule) {
		for(int i = rules.size() - 1; i >= 0; i--) {
			if (rules.get(i).label == rule.label) {
				rules.remove(i);
				return true;
			}
		}
		return false;
	}
}
