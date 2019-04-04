package com.araguacaima.braas.drools.Model;

import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Address;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;

public class Person extends io.codearte.jfairy.producer.person.Person {

    private final BeanUtilsBean beanUtilsBean = new BeanUtilsBean();

    public Person(String firstName, String middleName, String lastName, Address address, String email, String username, String password, Sex sex, String telephoneNumber, DateTime dateOfBirth, Integer age, String nationalIdentityCardNumber, String nationalIdentificationNumber, String passportNumber, Company company, String companyEmail) {
        super(firstName, middleName, lastName, address, email, username, password, sex, telephoneNumber, dateOfBirth, age, nationalIdentityCardNumber, nationalIdentificationNumber, passportNumber, company, companyEmail);
    }

    public void setAddress(Address address) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "address", address);
    }

    public void setFirstName(String firstName) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "firstName", firstName);
    }

    public void setMiddleName(String middleName) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "middleName", middleName);
    }

    public void setLastName(String lastName) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "lastName", lastName);
    }

    public void setEmail(String email) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "email", email);
    }

    public void setUsername(String username) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "username", username);
    }

    public void setPassword(String password) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "password", password);
    }

    public void setSex(Sex sex) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "sex", sex);
    }

    public void setTelephoneNumber(String telephoneNumber) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "telephoneNumber", telephoneNumber);
    }

    public void setDateOfBirth(DateTime dateOfBirth) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "dateOfBirth", dateOfBirth);
    }

    public void setAge(Integer age) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "age", age);
    }

    public void setCompany(Company company) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "company", company);
    }

    public void setCompanyEmail(String companyEmail) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "companyEmail", companyEmail);
    }

    public void setNationalIdentityCardNumber(String nationalIdentityCardNumber) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "nationalIdentityCardNumber", nationalIdentityCardNumber);
    }

    public void setNationalIdentificationNumber(String nationalIdentificationNumber) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "nationalIdentificationNumber", nationalIdentificationNumber);
    }

    public void setPassportNumber(String passportNumber) throws InvocationTargetException, IllegalAccessException {
        beanUtilsBean.setProperty(this, "passportNumber", passportNumber);
    }

}
