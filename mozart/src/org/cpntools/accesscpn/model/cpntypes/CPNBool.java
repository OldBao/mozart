/************************************************************************/
/* Access/CPN                                                           */
/* Copyright 2010-2011 AIS Group, Eindhoven University of Technology    */
/*                                                                      */
/* This library is free software; you can redistribute it and/or        */
/* modify it under the terms of the GNU Lesser General Public           */
/* License as published by the Free Software Foundation; either         */
/* version 2.1 of the License, or (at your option) any later version.   */
/*                                                                      */
/* This library is distributed in the hope that it will be useful,      */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of       */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    */
/* Lesser General Public License for more details.                      */
/*                                                                      */
/* You should have received a copy of the GNU Lesser General Public     */
/* License along with this library; if not, write to the Free Software  */
/* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,           */
/* MA  02110-1301  USA                                                  */
/************************************************************************/
package org.cpntools.accesscpn.model.cpntypes;

/**
 * @model
 * @author michael
 */
public interface CPNBool extends CPNType {
	/**
	 * @return the value of true
	 * @model required="false"
	 */
	public String getTrueValue();

	/**
	 * Sets the value of the '{@link org.cpntools.accesscpn.model.cpntypes.CPNBool#getTrueValue <em>True Value</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>True Value</em>' attribute.
	 * @see #getTrueValue()
	 * @generated
	 */
	void setTrueValue(String value);

	/**
	 * @return the value of false
	 * @model required="false"
	 */
	public String getFalseValue();

	/**
	 * Sets the value of the '{@link org.cpntools.accesscpn.model.cpntypes.CPNBool#getFalseValue <em>False Value</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>False Value</em>' attribute.
	 * @see #getFalseValue()
	 * @generated
	 */
	void setFalseValue(String value);
}
