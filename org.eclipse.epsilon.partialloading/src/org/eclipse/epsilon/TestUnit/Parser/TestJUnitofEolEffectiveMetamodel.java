package org.eclipse.epsilon.TestUnit.Parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.epsilon.SmartSaxParser.Demonstration;
import org.eclipse.epsilon.common.parse.Region;
import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.Junittest.main;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.Statement;
import org.eclipse.epsilon.eol.parse.Eol_EolParserRules.returnStatement_return;
import org.junit.Test;

public class TestJUnitofEolEffectiveMetamodel{

	String metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart.ecore";
	String model = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart2.xmi";
	EList<EObject> actualList;
	EList<EObject> expectedList;
	
	@Test
	public void allInstances() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllInstancesTest.eol";
		XMIN actualEf,expectedEf;

		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfKind("Node");
		expectedEf.getFromAllOfKind("Node").addToAttributes("name");
		
		/*Actual*/
		actualEf = main.calculation(eolFile);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}
	
	
	@Test
	public void AllofTypeforSubClass() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllofTypeforSubClassTest.eol";
		XMIN actualEf,expectedEf;
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfType("Action");
		expectedEf.getFromAllOfType("Action").addToAttributes("name");
		
		/*Actual*/
		actualEf = main.calculation(eolFile);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}

	@Test
	public void CallInOperation() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/CollectionofModelElementTest.eol";
		XMIN actualEf,expectedEf;
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfType("Action");
		expectedEf.getFromAllOfType("Action").addToAttributes("name");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		
		/*Actual*/
		actualEf = main.calculation(eolFile);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
		}
	
	@Test
	public void NonContainmentRefrence() throws Exception {
		
		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/NonContainmentRefrenceTest.eol";
		XMIN actualEf,expectedEf;
		
		/*Actual*/
		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
	//	actualEf = ef.getEffectiveMetamodel();
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("flowchart");
		expectedEf.addToAllOfKind("Node");
		expectedEf.getFromAllOfKind("Node").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");

		/*Actual*/
		actualEf = main.calculation(eolFile);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
}
	public String effectiveMetamodelConvertor(XMIN model) {
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
//	@Test
//	public void contaimentRefrence() throws Exception {
//		
//		metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/componentLanguage.ecore";
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/ContainmentRefrenceTest.eol";
//		
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//		//actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("componentLanguage");
//		expectedEf.addToAllOfKind("Component");
//	//	expectedEf.addReferenceToAllOfKind("Component","ports");
//		expectedEf.addToTypes("Port");
//		
//		//assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
//		}
//	@Test
//	public void EnumerationType() throws Exception {
//		
//		metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/componentLanguage.ecore";
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/EnumerationTypeTest.eol";
//		
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("componentLanguage");
//		expectedEf.addToAllOfKind("Component");
////		expectedEf.addReferenceToAllOfKind("Component","ports");
////		expectedEf.addToTypes("Port");
////		expectedEf.addAttributeToTypes("Port", "type");
////		
//	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
//		}
//	
//	

}



