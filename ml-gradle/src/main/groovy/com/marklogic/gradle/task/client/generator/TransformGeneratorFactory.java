/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.client.generator;

public class TransformGeneratorFactory {

	public enum TransformType {
		XQY,
		SJS,
		XSL
	}
	public static TransformGenerator getGenerator(TransformType type, String transformsPath, String[] rulesetNames) {
		if (TransformType.XQY.equals(type)) {
			return new XqueryTransformGenerator(transformsPath, rulesetNames);
		} else if (TransformType.SJS.equals(type)) {
			return new SjsTransformGenerator(transformsPath, rulesetNames);
		} else if (TransformType.XSL.equals(type)) {
			return new XslTransformGenerator(transformsPath, rulesetNames);
		}
		return null;
	}
}
