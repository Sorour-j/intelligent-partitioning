package org.eclipse.epsilon.effectivemetamodel.etl.test.unit;

import static org.junit.Assert.assertEquals;


import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.junit.Test;

public class TestJUnitofEtlEffectiveMetamodel{

	String metamodel = "src/org/eclipse/epsilon/effectivemetamodel/etl/test/unit/tree.ecore";
	
	@Test
	public void MethodContextTest() throws Exception {
		String etlFile = "src/org/eclipse/epsilon/effectivemetamodel/etl/test/unit/test.etl";
		EffectiveMetamodel actualEf = null, expectedEf;
		
		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.addToAllOfKind("Tree");
		expectedEf.getFromAllOfKind("Tree").addToAttributes("label");
		expectedEf.getFromAllOfKind("Tree").addToReferences("parent");
		
		/*actual*/
		actualEf = SetEtlCalculationSetting.calculation(etlFile , metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}

public String effectiveMetamodelConvertor(EffectiveMetamodel model) {
	String efMM = new String();
	for (EffectiveType type : model.getAllOfKind()) {
		efMM += "AllofKind: " + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			efMM += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			efMM += et.getName() + "-";
	}
	
	for (EffectiveType type : model.getAllOfType()) {
		efMM += "AllofType: " + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			efMM += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			efMM += et.getName() + "-";
	}
	
	for (EffectiveType type : model.getTypes()) {
		efMM += "types: " + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			efMM += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			efMM += et.getName() + "-";
	}
	
	return efMM;
	}
}