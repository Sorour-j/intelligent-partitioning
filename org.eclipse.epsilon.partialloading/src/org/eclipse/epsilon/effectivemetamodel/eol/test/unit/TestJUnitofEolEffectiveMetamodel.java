package org.eclipse.epsilon.effectivemetamodel.eol.test.unit;

import static org.junit.Assert.assertEquals;


import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.junit.Test;

public class TestJUnitofEolEffectiveMetamodel{

	protected String metamodel = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/flowchart.ecore";

	@Test
	public void allInstancesTest() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/AllInstancesTest.eol";
		EffectiveMetamodel actualEf,expectedEf;

		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfKind("Node");
		expectedEf.getFromAllOfKind("Node").addToAttributes("name");
		
		/*Actual*/
		actualEf = SetEolCalculationSetting.calculation(eolFile, metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}
	
	
	@Test
	public void AllofTypeTest() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/AllofTypeforSubClassTest.eol";
		EffectiveMetamodel actualEf,expectedEf;
		
		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfType("Action");
		expectedEf.getFromAllOfType("Action").addToAttributes("name");
		
		/*Actual*/
		actualEf = SetEolCalculationSetting.calculation(eolFile, metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}

	@Test
	public void CallInOperationTest() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/CollectionofModelElementTest.eol";
		EffectiveMetamodel actualEf,expectedEf;
		
		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfType("Action");
		expectedEf.getFromAllOfType("Action").addToAttributes("name");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		
		/*Actual*/
		actualEf = SetEolCalculationSetting.calculation(eolFile, metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
		}
	
	@Test
	public void NonContainmentRefrenceTest() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/NonContainmentRefrenceTest.eol";
		EffectiveMetamodel actualEf,expectedEf;
		
		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfKind("Node");
		expectedEf.getFromAllOfKind("Node").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");

		/*Actual*/
		actualEf = SetEolCalculationSetting.calculation(eolFile, metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
}
	@Test
	public void contaimentRefrenceTest() throws Exception {
		
		metamodel = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/componentLanguage.ecore";
		String eolFile = "src/org/eclipse/epsilon/effectivemetamodel/eol/test/unit/ContainmentRefrenceTest.eol";
		
		EffectiveMetamodel actualEf,expectedEf;
		
		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.addToAllOfKind("Component");
		expectedEf.getFromAllOfKind("Component").addToReferences("ports");
		expectedEf.addToAllOfKind("Port");
		
		/*Actual*/
		actualEf = SetEolCalculationSetting.calculation(eolFile, metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));

	
		}
	public String effectiveMetamodelConvertor(EffectiveMetamodel model) {
		String XMIN = null;
		for (EffectiveType type : model.getAllOfKind()) {
			XMIN += "AllofKind" + type.getName() + "-";
			for (EffectiveFeature et : type.getAttributes())
				XMIN += et.getName()+ "-";
			for (EffectiveFeature et : type.getReferences())
				XMIN += et.getName() + "-";
		}
		for (EffectiveType type : model.getAllOfType()) {
			XMIN += "AllofKind" + type.getName() + "-";
			for (EffectiveFeature et : type.getAttributes())
				XMIN += et.getName()+ "-";
			for (EffectiveFeature et : type.getReferences())
				XMIN += et.getName() + "-";
		}
		for (EffectiveType type : model.getTypes()) {
			XMIN += "AllofKind" + type.getName() + "-";
			for (EffectiveFeature et : type.getAttributes())
				XMIN += et.getName()+ "-";
			for (EffectiveFeature et : type.getReferences())
				XMIN += et.getName() + "-";
		}
		return XMIN;
	}
}



