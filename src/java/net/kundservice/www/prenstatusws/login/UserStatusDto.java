/**
 * UserStatusDto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.kundservice.www.prenstatusws.login;

public class UserStatusDto  implements java.io.Serializable {
    private java.lang.String username;

    private net.kundservice.www.prenstatusws.login.ProductDto[] products;

    private boolean isLoginOk;

    public UserStatusDto() {
    }

    public UserStatusDto(
           java.lang.String username,
           net.kundservice.www.prenstatusws.login.ProductDto[] products,
           boolean isLoginOk) {
           this.username = username;
           this.products = products;
           this.isLoginOk = isLoginOk;
    }


    /**
     * Gets the username value for this UserStatusDto.
     * 
     * @return username
     */
    public java.lang.String getUsername() {
        return username;
    }


    /**
     * Sets the username value for this UserStatusDto.
     * 
     * @param username
     */
    public void setUsername(java.lang.String username) {
        this.username = username;
    }


    /**
     * Gets the products value for this UserStatusDto.
     * 
     * @return products
     */
    public net.kundservice.www.prenstatusws.login.ProductDto[] getProducts() {
        return products;
    }


    /**
     * Sets the products value for this UserStatusDto.
     * 
     * @param products
     */
    public void setProducts(net.kundservice.www.prenstatusws.login.ProductDto[] products) {
        this.products = products;
    }


    /**
     * Gets the isLoginOk value for this UserStatusDto.
     * 
     * @return isLoginOk
     */
    public boolean isIsLoginOk() {
        return isLoginOk;
    }


    /**
     * Sets the isLoginOk value for this UserStatusDto.
     * 
     * @param isLoginOk
     */
    public void setIsLoginOk(boolean isLoginOk) {
        this.isLoginOk = isLoginOk;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserStatusDto)) return false;
        UserStatusDto other = (UserStatusDto) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.username==null && other.getUsername()==null) || 
             (this.username!=null &&
              this.username.equals(other.getUsername()))) &&
            ((this.products==null && other.getProducts()==null) || 
             (this.products!=null &&
              java.util.Arrays.equals(this.products, other.getProducts()))) &&
            this.isLoginOk == other.isIsLoginOk();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getUsername() != null) {
            _hashCode += getUsername().hashCode();
        }
        if (getProducts() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getProducts());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getProducts(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isIsLoginOk() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserStatusDto.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "UserStatusDto"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("username");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "Username"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("products");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "Products"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "ProductDto"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "ProductDto"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isLoginOk");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "IsLoginOk"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
