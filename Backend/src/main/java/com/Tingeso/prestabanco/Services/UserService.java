package com.Tingeso.prestabanco.Services;

import com.Tingeso.prestabanco.Entities.UserEntity;
import com.Tingeso.prestabanco.Repositories.LoanRequestRepository;
import com.Tingeso.prestabanco.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    LoanRequestRepository loanRequestRepository;

    // Create a new user
    /**
     * Method to save a user, it validates the user rut and email
     * @param user UserEntity
     * @return UserEntity
     */
    public UserEntity saveUser(UserEntity user){
        // Check if the user already exists
        if(userRepository.findByRut(user.getRut()) != null){
            return null;
        }
        if(userRepository.findByEmail(user.getEmail()) != null){
            return null;
        }
        // Verify if the user rut is valid
        if(!validateRut(user.getRut())){
            return null;
        }
        // Verify if the user email is valid
        if(!user.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
            return null;
        }
        // Format the user rut to xxxxxxxx-x
        user.setRut(user.getRut().replace(".", "").replace("-", ""));
        user.setRut(user.getRut().substring(0, user.getRut().length() - 1) + "-" + user.getRut().charAt(user.getRut().length() - 1));
        // Format the user email to lowercase
        user.setEmail(user.getEmail().toLowerCase());

        return userRepository.save(user);
    }

    /**
     * Method to validate the Chilean RUT
     * @param rut String
     * @return boolean
     */
    private boolean validateRut(String rut) {
        // Remove periods and hyphens from the RUT (xxx.xxx.xxx-x => xxxxxxxxx || xxxxxxxx-x => xxxxxxxx)
        rut = rut.replace(".", "").replace("-", "");

        // Extract the numeric part and the verification digit
        String rutNumber = rut.substring(0, rut.length() - 1);
        char verificationDigit = rut.charAt(rut.length() - 1);

        try {
            int rutInt = Integer.parseInt(rutNumber);

            // Calculate the verification digit
            char calculatedDV = calculateVerificationDigit(rutInt);

            // Compare the calculated digit with the provided one
            return calculatedDV == verificationDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Method to calculate the verification digit of the Chilean RUT
     * @param rut int
     * @return char
     */
    private char calculateVerificationDigit(int rut) {
        int sum = 0;
        int multiplier = 2;

        while (rut > 0) {
            int digit = rut % 10;
            sum += digit * multiplier;
            multiplier = (multiplier == 7) ? 2 : multiplier + 1;
            rut /= 10;
        }

        int remainder = 11 - (sum % 11);

        if (remainder == 11) {
            return '0';
        } else if (remainder == 10) {
            return 'K';
        } else {
            return (char) (remainder + '0');
        }
    }

    /**
     * get user by id
     * @param id Long
     * @return UserEntity
     */
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    /**
     * get user by rut
     * @param rut String
     * @return UserEntity
     */
    public UserEntity getUserByRut(String rut) {
        return userRepository.findByRut(rut);
    }

    /**
     * Method for login
     * @param user UserEntity
     * @return UserEntity
     */
    public UserEntity login(UserEntity user){
        UserEntity userDB = userRepository.findByEmail(user.getEmail());
        if(userDB != null){
            if(userDB.getPassword().equals(user.getPassword())){
                return userDB;
            }
        }
        return null;
    }

    /**
     * Method to find the role of a user
     * @param userId Long
     * @return Integer
     */
    public Integer findRolByUserId(Long userId) {
        return userRepository.findRolByUserId(userId);
    }

}
