/**
 */
package org.eclipse.epsilon.effectivemetamodel.neoemf;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Edge#getFrom <em>From</em>}</li>
 *   <li>{@link Edge#getTo <em>To</em>}</li>
 * </ul>
 *
 * @see GraphPackage#getEdge()
 * @model
 * @extends PersistentEObject
 * @generated
 */
public interface Edge extends PersistentEObject {
	/**
	 * Returns the value of the '<em><b>From</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>From</em>' reference.
	 * @see #setFrom(Vertice)
	 * @see GraphPackage#getEdge_From()
	 * @model
	 * @generated
	 */
	Vertice getFrom();

	/**
	 * Sets the value of the '{@link Edge#getFrom <em>From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>From</em>' reference.
	 * @see #getFrom()
	 * @generated
	 */
	void setFrom(Vertice value);

	/**
	 * Returns the value of the '<em><b>To</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>To</em>' reference.
	 * @see #setTo(Vertice)
	 * @see GraphPackage#getEdge_To()
	 * @model
	 * @generated
	 */
	Vertice getTo();

	/**
	 * Sets the value of the '{@link Edge#getTo <em>To</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To</em>' reference.
	 * @see #getTo()
	 * @generated
	 */
	void setTo(Vertice value);

} // Edge
