package com.cerner.devcenter.education.managers;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserSubscriptionDAO;
import com.cerner.devcenter.education.models.Learner;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * Class that tests the functionalities of {@link EmailManager}.
 *
 * @author Mani teja Kurapati (MK051340)
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailManagerTest {
    @InjectMocks
    private EmailManager emailManager;
    @Mock
    private JavaMailSender mockMailSender;
    @Mock
    private SimpleMailMessage mockTemplateMessage;
    @Mock
    private UserSubscriptionDAO mockUserSubscriptionDAO;
    @Mock
    private Resource mockResource;
    @Mock
    private ResourceType mockResourceType;
    @Mock
    private MimeMessage mockMessage;

    private URL url;
    private String[] emailIds;
    private List<Learner> learnerList;

    private static final String VALID_USER_ID = "MK051340";
    private static final String VALID_EMAIL_ID = "firstname.lastname@company.com";

    @Before
    public void setUp() {
        learnerList = new ArrayList<>();
        learnerList.add(new Learner(VALID_USER_ID, VALID_EMAIL_ID));
    }

    /**
     * Test {@link EmailManager#sendEmailWhenResourceAdded(Resource)} when null
     * resource object is passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSendEmailWhenResourceAdded() {
        emailManager.sendEmailWhenResourceAdded(null);
    }

    /**
     * Test {@link EmailManager#sendEmailWhenResourceAdded(Resource)} when
     * {@link MailException} is thrown expects {@link ManagerException}.
     */
    @Test(expected = ManagerException.class)
    public void testSendEmailWhenExceptionOccurs() throws MalformedURLException {
        url = new URL("http:\\www.google.com");
        emailIds = new String[1];
        emailIds[0] = "test.com";
        when(mockResource.getResourceType()).thenReturn(mockResourceType);
        when(mockResourceType.getResourceType()).thenReturn("ebook");
        when(mockResource.getDescription()).thenReturn("testDescription");
        when(mockResource.getResourceLink()).thenReturn(url);
        when(mockMailSender.createMimeMessage()).thenReturn(mockMessage);
        when(mockTemplateMessage.getFrom()).thenReturn("firstname.lastname@company.com");
        Mockito.doThrow(new MailException("test") {
        }).when(mockMailSender).send(mockMessage);
        emailManager.sendEmailWhenResourceAdded(mockResource);
    }

    /**
     * Test {@link EmailManager#sendEmailWhenResourceAdded(Resource)} with valid
     * input.
     */
    @Test
    public void testSendEmailWithValidInput() throws MalformedURLException {
        url = new URL("http:\\www.google.com");
        emailIds = new String[1];
        emailIds[0] = "test.com";
        when(mockResource.getResourceType()).thenReturn(mockResourceType);
        when(mockResourceType.getResourceType()).thenReturn("ebook");
        when(mockResource.getDescription()).thenReturn("testDescription");
        when(mockResource.getResourceLink()).thenReturn(url);
        when(mockMailSender.createMimeMessage()).thenReturn(mockMessage);
        when(mockTemplateMessage.getFrom()).thenReturn("firstname.lastname@company.com");
        Mockito.doNothing().when(mockMailSender).send(mockTemplateMessage);
        try {
            emailManager.sendEmailWhenResourceAdded(mockResource);
        } catch (ManagerException e) {
            fail("Exception not expected");
        }
    }
}
