package com.araguacaima.braas.core.drools.model;

import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Address;
import org.joda.time.DateTime;

import java.lang.reflect.Field;

import static com.araguacaima.braas.core.Commons.reflectionUtils;

public class Person extends io.codearte.jfairy.producer.person.Person {


    public Person(String firstName, String middleName, String lastName, Address address, String email, String username, String password, Sex sex, String telephoneNumber, DateTime dateOfBirth, Integer age, String nationalIdentityCardNumber, String nationalIdentificationNumber, String passportNumber, Company company, String companyEmail) {
        super(firstName, middleName, lastName, address, email, username, password, sex, telephoneNumber, dateOfBirth, age, nationalIdentityCardNumber, nationalIdentificationNumber, passportNumber, company, companyEmail);
    }

    public void setAddress(Address address) {
        reflectionUtils.invokeSimpleSetter(this, "address", address);
    }

    public void setFirstName(String firstName) throws IllegalAccessException {
        Field field = reflectionUtils.getField(this, "firstName");
        field.setAccessible(true);
        field.set(this, firstName);
    }

    public void setMiddleName(String middleName) {
        reflectionUtils.invokeSimpleSetter(this, "middleName", middleName);
    }

    public void setLastName(String lastName) {
        reflectionUtils.invokeSimpleSetter(this, "lastName", lastName);
    }

    public void setEmail(String email) {
        reflectionUtils.invokeSimpleSetter(this, "email", email);
    }

    public void setUsername(String username) {
        reflectionUtils.invokeSimpleSetter(this, "username", username);
    }

    public void setPassword(String password) {
        reflectionUtils.invokeSimpleSetter(this, "password", password);
    }

    public void setSex(Sex sex) {
        reflectionUtils.invokeSimpleSetter(this, "sex", sex);
    }

    public void setTelephoneNumber(String telephoneNumber) {
        reflectionUtils.invokeSimpleSetter(this, "telephoneNumber", telephoneNumber);
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        reflectionUtils.invokeSimpleSetter(this, "dateOfBirth", dateOfBirth);
    }

    public void setAge(Integer age) {
        reflectionUtils.invokeSimpleSetter(this, "age", age);
    }

    public void setCompany(Company company) {
        reflectionUtils.invokeSimpleSetter(this, "company", company);
    }

    public void setCompanyEmail(String companyEmail) {
        reflectionUtils.invokeSimpleSetter(this, "companyEmail", companyEmail);
    }

    public void setNationalIdentityCardNumber(String nationalIdentityCardNumber) {
        reflectionUtils.invokeSimpleSetter(this, "nationalIdentityCardNumber", nationalIdentityCardNumber);
    }

    public void setNationalIdentificationNumber(String nationalIdentificationNumber) {
        reflectionUtils.invokeSimpleSetter(this, "nationalIdentificationNumber", nationalIdentificationNumber);
    }

    public void setPassportNumber(String passportNumber) {
        reflectionUtils.invokeSimpleSetter(this, "passportNumber", passportNumber);
    }

    public static class PersonWrapper {
        public static Person fromParent(io.codearte.jfairy.producer.person.Person person) {
            return new Person(person.getFirstName(),
                    person.getMiddleName(),
                    person.getLastName(),
                    person.getAddress(),
                    person.getEmail(),
                    person.getUsername(),
                    person.getPassword(),
                    person.getSex(),
                    person.getTelephoneNumber(),
                    person.getDateOfBirth(),
                    person.getAge(),
                    person.getNationalIdentityCardNumber(),
                    person.getNationalIdentificationNumber(),
                    person.getPassportNumber(),
                    person.getCompany(),
                    person.getCompanyEmail());
        }
    }

}
