package com.cerner.devcenter.education.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserSubscriptionDAO;
import com.cerner.devcenter.education.models.Learner;
import com.cerner.devcenter.education.models.Resource;
import com.google.common.base.Preconditions;

/**
 * This class provides services for sending email's to subscriber's.
 *
 * @author Mani teja Kurapati (MK051340).
 *
 */
@Service("emailManager")
public class EmailManager {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SimpleMailMessage templateMessage;
    @Autowired
    private UserSubscriptionDAO userSubscriptionDAO;
    @Autowired
    private ResourceTypeManager resourceTypeManager;

    private static final String ERROR_GETTING_EMAIL_LIST = "Error retrieving email addresees of subscribed users";
    private static final String ERROR_SENDING_EMAIL = "Error sending email to recipients";
    private static final String INVALID_RESOURCE = "Resource object cannot be null";
    private static final String MESSAGE_PART1 = "email.message.part1";
    private static final String MESSAGE_PART2 = "email.message.part2";
    private static final String MESSAGE_PART3 = "email.message.part3";
    private static ResourceBundle i18nBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());

    public EmailManager() {

    }

    /**
     * This method notify via email about new resource added to all the learners
     * who have subscribed to categories belonging to the added resource.
     *
     * @param resource
     *            {@link Resource} object which contains information about new
     *            added resource.
     * @throws ManagerException
     *             When there is an error sending email.
     */
    public void sendEmailWhenResourceAdded(Resource resource) {
        Preconditions.checkArgument(resource != null, INVALID_RESOURCE);
        String messageBody = i18nBundle.getString(MESSAGE_PART1) + resource.getResourceType().getResourceTypeId() + " "
                + i18nBundle.getString(MESSAGE_PART2) + " " + resource.getDescription() + " "
                + i18nBundle.getString(MESSAGE_PART3) + resource.getResourceLink();
        List<String> emailAddresses = getEmailAddressesOfSubscribedUsers(resource);
        String[] emailArray = new String[emailAddresses.size()];
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(templateMessage.getFrom());
            helper.setTo(emailAddresses.toArray(emailArray));
            helper.setSubject(resource.getDescription());
            helper.setText(messageBody);
            mailSender.send(message);
        } catch (MailException | MessagingException exception) {
            throw new ManagerException(ERROR_SENDING_EMAIL, exception);
        }
    }

    /**
     * This method returns an list of email addresses for whom email have to
     * sent to notify new resource addition.
     *
     * @param resource
     *            {@link Resource} object which contains resource information
     *            which should include resource id.
     * @return {@link List} of email addresses of subscribed users.
     * @throws ManagerException
     *             when there is an error retrieving all email addresses of
     *             subscribed users.
     */
    private List<String> getEmailAddressesOfSubscribedUsers(Resource resource) {
        Preconditions.checkArgument(resource != null, INVALID_RESOURCE);
        List<Learner> learnersList;
        try {
            learnersList = userSubscriptionDAO.getLearnersSubscribedToCategoriesBelongingToResource(resource);
        } catch (DAOException daoException) {
            throw new ManagerException(ERROR_GETTING_EMAIL_LIST, daoException);
        }
        List<String> emailList = new ArrayList<>();
        for (Learner learnerIterate : learnersList) {
            emailList.add(learnerIterate.getEmailId());
        }
        return emailList;
    }
}
