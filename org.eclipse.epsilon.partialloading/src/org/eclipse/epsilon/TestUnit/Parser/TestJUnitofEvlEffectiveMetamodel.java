package org.eclipse.epsilon.TestUnit.Parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractionforEVL;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.Statement;
import org.eclipse.epsilon.eol.parse.Eol_EolParserRules.returnStatement_return;
import org.junit.Test;

public class TestJUnitofEvlEffectiveMetamodel {

	String metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/java.ecore";
	// String model = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart2.xmi";
	EList<EObject> actualList;
	EList<EObject> expectedList;

	@Test
	public void allInstances() throws Exception {

		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/allInstancesTest.evl";
		XMIN actualEf, expectedEf;

		/* Actual */
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();

		/* Expected */
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("ImportDeclaration");
//		expectedEf.addReferenceToAllOfKind("ImportDeclaration", "importedElement");
//		expectedEf.addToAllOfKind("NamedElement");
//
//		// change the message*////
//		assertEquals("Failed", compare(actualEf, expectedEf), true);

	//	actualEf.load();
	}

	@Test
	public void AllofTypeforSubClass() throws Exception {

		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllofTypeforSubClassTest.evl";
		XMIN actualEf, expectedEf;

		/* Actual */
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();

		/* Expected */
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("ImportDeclaration");
//		expectedEf.addReferenceToAllOfKind("ImportDeclaration", "importedElement");
//		expectedEf.addToAllOfType("Package");
//		assertEquals("Failed", compare(actualEf, expectedEf), true);

	}

	@Test
	public void AllofTypeforSuperClass() throws Exception {

		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllofTypeforSuperClassTest.evl";
		XMIN actualEf, expectedEf;

		/* Actual */
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();

		/* Expected */
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("ImportDeclaration");
//		expectedEf.addReferenceToAllOfKind("ImportDeclaration", "importedElement");
//		expectedEf.addToAllOfType("NamedElement");
//		assertEquals("Failed", compare(actualEf, expectedEf), true);

	}

	@Test
	public void CollectionofModelElementTest() throws Exception {

		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/CollectionofModelElementTest.evl";
		XMIN actualEf, expectedEf;

		/* Actual */
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();

		/* Expected */
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("ImportDeclaration");
		expectedEf.addToAllOfKind("Package");
//		expectedEf.addReferenceToAllOfKind("ImportDeclaration", "importedElement");
//		expectedEf.addAttributeToAllOfKind("Package", "name");
//		expectedEf.addToTypes("NamedElement");
//		expectedEf.addAttributeToTypes("NamedElement", "name");
//		assertEquals("Failed", compare(actualEf, expectedEf), true);
	}
//	
	@Test
	public void NonContainmentRefrence() throws Exception {
		
		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/NonContainmentRefrenceTest.evl";
		XMIN actualEf,expectedEf;
		
		/*Actual*/
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();
		
		/* Expected */
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("ImportDeclaration");
		//expectedEf.addReferenceToAllOfKind("ImportDeclaration", "importedElement");
		
	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
	
		}
	
	@Test
	public void subClass() throws Exception {
		
		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/SubClassTest.evl";
		XMIN actualEf,expectedEf;
		
		/*Actual*/
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("Type");
		//expectedEf.addAttributeToAllOfKind("Type", "name");
		
	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
	
		}
	
	@Test
	public void superClass() throws Exception {
		
		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/SuperClassTest.evl";
		XMIN actualEf,expectedEf;
		
		/*Actual*/
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("NamedElement");
//		expectedEf.addAttributeToAllOfKind("NamedElement", "name");
//		
//		assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
		}
	@Test
	public void contaimentRefrence() throws Exception {
		
		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/ContainmentRefrenceTest.evl";
		
		XMIN actualEf,expectedEf;
		
		/*Actual*/
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("Package");
		//expectedEf.addReferenceToAllOfKind("Package","ownedElements");
		//expectedEf.addToTypes("AbstractTypeDeclaration");
		
		//assertEquals("Failed", compare (actualEf , expectedEf), true);
	
		}
	@Test
	public void EnumerationType() throws Exception {
		
		String evlFile = "src/org/eclipse/epsilon/TestUnit/Parser/EnumerationTypeTest.evl";
		
		XMIN actualEf,expectedEf;
		
		/*Actual*/
		EffectiveMetamodelExtractionforEVL ef = new EffectiveMetamodelExtractionforEVL(metamodel, evlFile);
		actualEf = ef.getEffectiveMetamodel();
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.setName("javaMM");
		expectedEf.addToAllOfKind("BodyDeclaration");
//		expectedEf.addReferenceToAllOfKind("BodyDeclaration","modifier");
//		expectedEf.addToTypes("Modifier");
//		expectedEf.addAttributeToTypes("Modifier", "visibility");
//		
//		assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
		}
	

	public static boolean compare(XMIN actual, XMIN expected) {

		int i = 0;
		int j = 0;
		EffectiveType actualtype;
		EffectiveFeature actualfeature;

		// Check AllofKind
		if (actual.getAllOfKind().size() == expected.getAllOfKind().size()) {
			
			for (EffectiveType expectedtype : expected.getAllOfKind()) {
					j=0;
				if (actual.getFromAllOfKind(expectedtype.getName()) != null
						&& actual.getFromAllOfKind(expectedtype.getName()).isequals(expectedtype)) {
					
					actualtype = actual.getFromAllOfKind(expectedtype.getName());

					for (EffectiveFeature expectedfeature : expectedtype.getAllFeatures()) {
						actualfeature = actualtype.getAllFeatures().get(j);

						if (!actualfeature.equals(expectedfeature))
							return false;
						else
							j++;
					}
				} else
					return false;

			}
		} else
			return false;

		// Check AllofType
		if (actual.getAllOfType().size() == expected.getAllOfType().size()) {
			for (EffectiveType expectedtype : expected.getAllOfType()) {

				
				j = 0;
				if (actual.getFromAllOfType(expectedtype.getName()) != null
						&& actual.getFromAllOfType(expectedtype.getName()).isequals(expectedtype)) {
					
					actualtype = actual.getFromAllOfType(expectedtype.getName());

					for (EffectiveFeature expectedfeature : expectedtype.getAllFeatures()) {
						actualfeature = actualtype.getAllFeatures().get(j);

						if (!actualfeature.equals(expectedfeature))
							return false;
						else
							j++;
					}

				} else
					return false;

			}
		} else
			return false;

		// Check Types
		if (actual.getTypes().size() == expected.getTypes().size()) {
			for (EffectiveType expectedtype : expected.getTypes()) {

				j = 0;

				if (actual.getFromTypes(expectedtype.getName()) != null
						&& actual.getFromTypes(expectedtype.getName()).isequals(expectedtype)) {
					
					actualtype = actual.getFromTypes(expectedtype.getName());
					
					for (EffectiveFeature expectedfeature : expectedtype.getAllFeatures()) {
						actualfeature = actualtype.getAllFeatures().get(j);

						if (!actualfeature.equals(expectedfeature))
							return false;
						else
							j++;
					}
					i++;

				} else
					return false;

			}

			return true;
		}

		else
			return false;
	}
}
