package com.cerner.devcenter.education.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.TagManager;
import com.cerner.devcenter.education.models.AddTagsToResourceRequest;
import com.cerner.devcenter.education.models.Tag;
import com.cerner.devcenter.education.utils.Constants;

/**
 * This class defines the controller that handles requests related to retrieving
 * tags.
 *
 * @author Amos Bailey (AB032627)
 */

@Controller
@RequestMapping("/app")
public class TagController {

    @Autowired
    private TagManager tagManager;
    private static final String INVALID_SEARCH = "Search string cannot be null or empty.";
    private static final Logger LOGGER = Logger.getLogger(TagController.class);

    /**
     * Handles the request for searching tags. Retrieves a list of tag names
     * which contain the search string and sorts them alphabetically. Returns 10
     * or fewer results per request.
     * 
     * @param search
     *            string user entered to perform search (cannot be null, empty
     *            or blank).
     * @return {@link List} of tag names (cannot be null).
     * @throws IllegalArgumentException
     *             when invalid/blank search keyword is entered.
     */
    @RequestMapping(value = "/tagAutocomplete", method = RequestMethod.GET)
    public @ResponseBody List<Tag> tagAutocomplete(@RequestParam("search") final String search) {
        checkArgument(StringUtils.isNotBlank(search), INVALID_SEARCH);
        List<Tag> tagList = tagManager.getSearchedTags(search.toLowerCase());
        // resize 'tagList' if it has more items than 'AUTOFILLSIZE' entries
        if (tagList.size() > Constants.AUTOFILL_SIZE) {
            return tagList.subList(0, Constants.AUTOFILL_SIZE);
        }
        return tagList;
    }

    /**
     * Handles the request for adding tags to a resource.
     * 
     * @param resourceTagsRequest
     *            An {@link AddTagsToResourceRequest} object containing a list
     *            of tag names and the ID of the resource to add it to.
     * @return A boolean value, stating whether or not the tags were
     *         successfully added.
     * @throws IllegalArgumentException
     *             When the request contains an empty tag list.
     */
    @RequestMapping(value = "/addTagsToResource", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody boolean addTagsToResource(@RequestBody AddTagsToResourceRequest resourceTagsRequest) {
        checkArgument(resourceTagsRequest != null, Constants.ADD_TAGS_TO_RESOURCE_REQUEST_NULL);
        List<String> tagList = resourceTagsRequest.getTagNameList();
        checkArgument(!tagList.isEmpty(), Constants.TAG_LIST_EMPTY);
        try {
            tagManager.addTagsToResource(new HashSet<String>(resourceTagsRequest.getTagNameList()),
                    resourceTagsRequest.getResourceID());
            return true;
        } catch (ManagerException managerException) {
            LOGGER.error(Constants.ERROR_ADDING_TAGS);
            return false;
        }
    }
}
