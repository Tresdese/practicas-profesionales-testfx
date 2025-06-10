package logic.validators;

import logic.exceptions.InvalidData;
import logic.utils.EmailValidator;
import logic.utils.PhoneValidator;
import logic.utils.TuitionValidator;

public class StudentValidator {
    public static void validateStudentData(String tuiton, String email, String phone) throws InvalidData {
        TuitionValidator.validate(tuiton);
        EmailValidator.validate(email);
        PhoneValidator.validate(phone);
    }

    public static void validateStudentData(String email, String phone ) throws InvalidData {
        EmailValidator.validate(email);
        PhoneValidator.validate(phone);
    }
}