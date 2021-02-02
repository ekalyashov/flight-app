//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.30 at 12:57:23 PM MSK 
//


package cselp.domain.opensky;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MEMBERType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MEMBERType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FULL_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DIVISION" type="{}DIVISIONType"/>
 *         &lt;element name="GENDER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FIRST_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LAST_NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATE_OF_BIRTH" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="MINIMUMS" type="{}MINIMUMSType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MEMBERType", propOrder = {
    "no",
    "fullname",
    "division",
    "gender",
    "firstname",
    "lastname",
    "dateofbirth",
    "minimums"
})
public class MEMBERType {

    @XmlElement(name = "NO", required = true)
    protected String no;
    @XmlElement(name = "FULL_NAME", required = true)
    protected String fullname;
    @XmlElement(name = "DIVISION", required = true)
    protected DIVISIONType division;
    @XmlElement(name = "GENDER", required = true)
    protected String gender;
    @XmlElement(name = "FIRST_NAME", required = true)
    protected String firstname;
    @XmlElement(name = "LAST_NAME", required = true)
    protected String lastname;
    @XmlElement(name = "DATE_OF_BIRTH", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateofbirth;
    @XmlElement(name = "MINIMUMS", required = true)
    protected MINIMUMSType minimums;

    /**
     * Gets the value of the no property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNO() {
        return no;
    }

    /**
     * Sets the value of the no property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNO(String value) {
        this.no = value;
    }

    /**
     * Gets the value of the fullname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFULLNAME() {
        return fullname;
    }

    /**
     * Sets the value of the fullname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFULLNAME(String value) {
        this.fullname = value;
    }

    /**
     * Gets the value of the division property.
     * 
     * @return
     *     possible object is
     *     {@link DIVISIONType }
     *     
     */
    public DIVISIONType getDIVISION() {
        return division;
    }

    /**
     * Sets the value of the division property.
     * 
     * @param value
     *     allowed object is
     *     {@link DIVISIONType }
     *     
     */
    public void setDIVISION(DIVISIONType value) {
        this.division = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGENDER() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGENDER(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIRSTNAME() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIRSTNAME(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the lastname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLASTNAME() {
        return lastname;
    }

    /**
     * Sets the value of the lastname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLASTNAME(String value) {
        this.lastname = value;
    }

    /**
     * Gets the value of the dateofbirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDATEOFBIRTH() {
        return dateofbirth;
    }

    /**
     * Sets the value of the dateofbirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDATEOFBIRTH(XMLGregorianCalendar value) {
        this.dateofbirth = value;
    }

    /**
     * Gets the value of the minimums property.
     * 
     * @return
     *     possible object is
     *     {@link MINIMUMSType }
     *     
     */
    public MINIMUMSType getMINIMUMS() {
        return minimums;
    }

    /**
     * Sets the value of the minimums property.
     * 
     * @param value
     *     allowed object is
     *     {@link MINIMUMSType }
     *     
     */
    public void setMINIMUMS(MINIMUMSType value) {
        this.minimums = value;
    }

}