package org.eclipse.epsilon.effectivemetamodel;

import java.util.ArrayList;

import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.compile.m3.Attribute;
import org.eclipse.epsilon.eol.compile.m3.MetaClass;
import org.eclipse.epsilon.eol.compile.m3.Reference;
import org.eclipse.epsilon.eol.compile.m3.StructuralFeature;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.PropertyCallExpression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.eclipse.epsilon.eol.staticanalyser.EolStaticAnalyser;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.eol.types.EolType;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.staticanalyser.EvlStaticAnalyser;

public class EffectiveMetamodelExtractor {

	XMIN smartEMFModel = null;
	ArrayList<ModuleElement> children = new ArrayList<ModuleElement>();
	EffectiveType effectiveType;
	EolModelElementType target;

	public EffectiveMetamodelExtractor() {
	}

	public XMIN geteffectiveMetamodel(IEolModule module) {

		module.getCompilationContext().setModelFactory(new SubModelFactory());
		if (module instanceof EvlModule)
			new EvlStaticAnalyser().validate(module);
		else
			new EolStaticAnalyser().validate(module);

		try {
			smartEMFModel = (XMIN) module.getContext().getModelRepository().getModelByName(
					module.getCompilationContext().getModelDeclarations().get(0).getNameExpression().getName());
		} catch (EolModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		children.addAll(module.getChildren());

		while (!(children.isEmpty())) {

			ModuleElement MD = children.get(0);
			children.remove(MD);

			if ((!(MD.getChildren().isEmpty())))
				children.addAll(MD.getChildren());

			if (MD instanceof ConstraintContext && ((ConstraintContext) MD).getTypeExpression() != null) {

				target = (EolModelElementType) ((ConstraintContext) MD).getTypeExpression().getResolvedType();
				smartEMFModel.addToAllOfKind(target.getName());

				if (smartEMFModel.allOfTypeContains(target.getTypeName())) {

					smartEMFModel.addToAllOfKind(smartEMFModel.getFromAllOfType(target.getName()));
					smartEMFModel.removeFromAllOfType(target.getName());

				}
				// If the element is already under EM's types reference
				if (smartEMFModel.typesContains(target.getTypeName())) {

					smartEMFModel.addToAllOfKind(smartEMFModel.getFromTypes(target.getName()));
					smartEMFModel.removeFromTypes(target.getName());
				} else {
					// not already under the EM's allOfKind or allOfType references
					smartEMFModel.addToAllOfKind(target.getTypeName());
				}
			}

			if (MD instanceof OperationCallExpression) {

				OperationCallExpression operationCall = (OperationCallExpression) MD;

				if (!operationCall.isContextless()) {

					if (operationCall.getTargetExpression().getResolvedType() instanceof EolModelElementType) {

						target = (EolModelElementType) operationCall.getTargetExpression().getResolvedType();

						if (operationCall.getNameExpression().getName().equals("all")
								|| operationCall.getNameExpression().getName().equals("allOfKind")
								|| operationCall.getNameExpression().getName().equals("allInstances")) {

							// If the element is already under EM's allOfType reference
							if (smartEMFModel.allOfTypeContains(target.getTypeName())) {

								smartEMFModel.addToAllOfKind(smartEMFModel.getFromAllOfType(target.getName()));
								smartEMFModel.removeFromAllOfType(target.getName());
							}
							// If the element is already under EM's types reference
							if (smartEMFModel.typesContains(target.getTypeName())) {

								smartEMFModel.addToAllOfKind(smartEMFModel.getFromTypes(target.getName()));
								smartEMFModel.removeFromTypes(target.getName());
							} else {
								// not already under the EM's allOfKind or allOfType references
								smartEMFModel.addToAllOfKind(target.getTypeName());
							}

						} else if (operationCall.getNameExpression().getName().equals("allOfType")) {
							if (!smartEMFModel.allOfKindContains(target.getTypeName())
									&& !smartEMFModel.allOfTypeContains(target.getTypeName())) {
								smartEMFModel.addToAllOfType(target.getTypeName());
							}
						}
					}
				}
			}
			if (MD instanceof PropertyCallExpression) {

				PropertyCallExpression pro = (PropertyCallExpression) MD;
				EolType resolvedType = pro.getTargetExpression().getResolvedType();

				if (resolvedType instanceof EolModelElementType)
					ProprtyCallExpresionHandler(pro, ((EolModelElementType) resolvedType));

				else if (resolvedType instanceof EolCollectionType
						&& ((EolCollectionType) resolvedType).getContentType() instanceof EolModelElementType)
					ProprtyCallExpresionHandler(pro,
							((EolModelElementType) ((EolCollectionType) resolvedType).getContentType()));

				else if (resolvedType instanceof EolAnyType && !pro.getTargetExpression().getPossibleType().isEmpty()) {
					for (EolType t : pro.getTargetExpression().getPossibleType())
						if (t instanceof EolModelElementType)
							ProprtyCallExpresionHandler(pro, (EolModelElementType) t);
						else if (t instanceof EolCollectionType
								&& ((EolCollectionType) t).getContentType() instanceof EolModelElementType)
							ProprtyCallExpresionHandler(pro,
									((EolModelElementType) ((EolCollectionType) t).getContentType()));
				}

			}
		}

		smartEMFModel.setCalculatedEffectiveMetamodel(true);
		smartEMFModel.load();
		return smartEMFModel;
	}

	public void ProprtyCallExpresionHandler(PropertyCallExpression pro, EolModelElementType resolvedType) {

		ArrayList<StructuralFeature> features = new ArrayList<StructuralFeature>();
		target = resolvedType;

		if (pro.getNameExpression().getName().equals("all")
				|| pro.getNameExpression().getName().equals("allInstances")) {
			// Like AllofKind Algorithm

			// If the element is already under EM's allOfType reference
			if (smartEMFModel.allOfTypeContains(target.getTypeName())) {

				smartEMFModel.addToAllOfKind(smartEMFModel.getFromAllOfType(target.getName()));
				smartEMFModel.removeFromAllOfType(target.getName());
			}
			// If the element is already under EM's types reference
			if (smartEMFModel.typesContains(target.getTypeName())) {

				smartEMFModel.addToAllOfKind(smartEMFModel.getFromTypes(target.getName()));
				smartEMFModel.removeFromTypes(target.getName());
			} else {

				// not already under the EM's allOfKind or allOfType references
				smartEMFModel.addToAllOfKind(target.getTypeName());
			}
		}
		// not already under the EM's types, allOfKind or allOfType references
		else {
			effectiveType = new EffectiveType(target.getTypeName());
			effectiveType.setEffectiveMetamodel(smartEMFModel);

			if (smartEMFModel.allOfKindContains(effectiveType.getName()))
				effectiveType = smartEMFModel.getFromAllOfKind(effectiveType.getName());
			
			else if (smartEMFModel.allOfTypeContains(effectiveType.getName()))
				effectiveType = smartEMFModel.getFromAllOfType(effectiveType.getName());
			
			else if (smartEMFModel.typesContains(effectiveType.getName()))
				effectiveType = smartEMFModel.getFromTypes(effectiveType.getName());
			
			else {
				// add target.getTypeName() under EM's types reference;
				effectiveType = smartEMFModel.addToTypes(effectiveType.getName());
			}
			if (!target.getMetaClass().getAllStructuralFeatures().isEmpty()) {
				features.addAll(target.getMetaClass().getAllStructuralFeatures());
			}

			for (MetaClass metaclass : target.getMetaClass().getSuperTypes()) {
				features.addAll(metaclass.getAllStructuralFeatures());
			}

			for (StructuralFeature sf : features) {
				if (sf instanceof Attribute && sf.getName().equals(pro.getNameExpression().getName())
						&& !effectiveType.containsAttribute(sf.getName())) {
					effectiveType.addToAttributes(sf.getName());

					break;
				} else if (sf instanceof Reference && sf.getName().equals(pro.getNameExpression().getName())
						&& !effectiveType.containsReference(sf.getName())) {

				/*	if (pro.getResolvedType() instanceof EolCollectionType
							&& sf.getType().getName().equals(((EolCollectionType) (pro.getResolvedType())).getContentType().getName())
							|| sf.getType().getName().equals(pro.getResolvedType().getName())
							|| !pro.getTargetExpression().getPossibleType().isEmpty()) {
						*/
						effectiveType.addToReferences(sf.getName());

						if (!((Reference) sf).isContainment()) {

							//if (sf.getType() instanceof EolCollectionType)
//								smartEMFModel.addToAllOfKind(
//										((EolCollectionType) (sf.getType())).getContentType().getName());
//							else
								smartEMFModel.addToAllOfKind(sf.getType().getName());
						}
					//}
				}
			}
		}
	}
}
