package com.cerner.devcenter.education.managers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class contains methods for actions required by category customization for the teams.
 * @author AC034492
 */
public class CategoryCustomizer {

    /**
     * enum for the tag names in the XML
     * <pre>
     * {@code
     * Sample XML Structure
     *
     * <TeamCategories>
     *      <team id="1">
     *           <teamName>Dev Academy</teamName>
     *          <category category_id="1">
     *              <categoryName>Patient Confidentiality</categoryName>
     *              <category-pref>2</category-pref>
     *          </category>
     *      </team>
     * </TeamCategories>
     * }
     * </pre>
     * @author AC034492
     *
     */
    private enum DocumentTags {

        ROOT_NODE("TeamCategories"), TEAM("team"), TEAM_NAME("teamName"),
        CATEGORY("category"), CATEGORY_NAME("categoryName"), CATEGORY_PREF("category-pref");

        private String tagName;

        private DocumentTags(final String tagName) {
            this.tagName = tagName;
        }

        public String getTagName() {
            return tagName;
        }
    }

    private XPath xPath = XPathFactory.newInstance().newXPath();

    /**
     * This method checks if the given category is present in the customization list for the team
     * @param document Document object holding all the customization information read from the XML
     * @param teamName Name of the team
     * @param categoryName Name of the category
     * @return true if the category is already selected by the team else it will be false.
     * @throws XPathExpressionException
     */
    public boolean checkIfTeamHasCategory(Document document, String teamName, String categoryName) throws XPathExpressionException{

        StringBuilder searchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                DocumentTags.TEAM.getTagName() + "[" +
                DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]/" +
                DocumentTags.CATEGORY.getTagName() + "[" +
                DocumentTags.CATEGORY_NAME.getTagName() + "/text()[contains(.,'" + categoryName  + "')]]");

        return getNodeList(document, searchBuilder.toString()).getLength() == 1 ? true : false;
    }

    /**
     * This methods checks if the given team is already having customization
     * @param doc  Document object holding all the customization information read from the XML
     * @param teamName  teamName that need to be verified
     * @return returns true if team is already having customization or else returns false.
     * @throws XPathExpressionException
     */

    public boolean checkIfTeamHasCustomization(Document doc, String teamName) throws XPathExpressionException{

        StringBuilder searchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                DocumentTags.TEAM.getTagName() + "[" +
                DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]");

        return getNodeList(doc, searchBuilder.toString()).getLength() == 1 ? true : false;
    }

    /**
     * This method returns a NodeList after parsing the document object with XPath evaluate
     * @param document Document object holding all the customization information read from the XML
     * @param searchString string with hierarchy of tags that need to be searched ( usage : "//TeamsCategories/team" for list of all team nodes)
     * @return a Node list
     * @throws XPathExpressionException
     */
    private NodeList getNodeList(Document document, String searchString) throws XPathExpressionException {
        XPathExpression expr = xPath.compile(searchString);
        return (NodeList) expr.evaluate(document, XPathConstants.NODESET);
    }

    /**
     * This method adds a given category to the customization list for the team. This methods also ensures
     * not to add duplicate categories to the team
     * @param document Document object holding all the customization information read from the XML
     * @param teamName Name of the team for which the category need to be included.
     * @param categoryName - Name of the category
     * @param preference - preference number for the category
     * @return an object of type Document holding the updated information is returned
     * @throws DOMException
     * @throws XPathExpressionException
     */
    public Document addCategoryForTeam(Document document, String teamName, String categoryName, int preference) throws XPathExpressionException, DOMException{

        StringBuilder searchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                DocumentTags.TEAM.getTagName() + "[" +
                DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]");

        NodeList nodeList = getNodeList(document, searchBuilder.toString());

        Element team = (Element) nodeList.item(0);

        if(!checkIfTeamHasCategory(document, teamName, categoryName)){
            Element category = document.createElement(DocumentTags.CATEGORY.getTagName());
            category.setAttribute("category_id", Integer.toString(team.getElementsByTagName(DocumentTags.CATEGORY.getTagName()).getLength() + 1));
            Element categoryNameElement = document.createElement(DocumentTags.CATEGORY_NAME.getTagName());
            categoryNameElement.appendChild(document.createTextNode(categoryName));
            category.appendChild(categoryNameElement);

            Element categoryPrefElement = document.createElement(DocumentTags.CATEGORY_PREF.getTagName());
            categoryPrefElement.appendChild(document.createTextNode(Integer.toString(preference)));
            category.appendChild(categoryPrefElement);

            team.appendChild(category);
        }

        return document;
    }

    /**
     * This method is for adding a Team in to the customization list
     * @param doc Document object holding all the customization information read from the XML
     * @param teamName Name of the team that need to be included
     * @return an object of type Document holding the updated information is returned
     * @throws DOMException
     * @throws XPathExpressionException
     */
    public Document addTeamForCustomization(Document doc, String teamName) throws XPathExpressionException, DOMException{

        Node rootNode = doc.getElementsByTagName(DocumentTags.ROOT_NODE.getTagName()).item(0);

        if(!checkIfTeamHasCustomization(doc, teamName)){

            NodeList nodeList = doc.getElementsByTagName(DocumentTags.TEAM.getTagName());

            Element newTeam = doc.createElement(DocumentTags.TEAM.getTagName());
            newTeam.setAttribute("id", Integer.toString(nodeList.getLength() + 1));

            Element nameOfTeam = doc.createElement(DocumentTags.TEAM_NAME.getTagName());
            nameOfTeam.appendChild(doc.createTextNode(teamName));

            newTeam.appendChild(nameOfTeam);
            rootNode.appendChild(newTeam);
        }
        return doc;
    }

    /**
     * This method is for deleting a team from the customization list
     * @param doc Document object holding all the customization information read from the XML
     * @param teamName Name of the team that need to be included
     * @return an object of type Document holding the updated information is returned
     * @throws DOMException
     * @throws XPathExpressionException
     */
    public Document deleteTeamFromCustomization(Document doc, String teamName) throws XPathExpressionException, DOMException{

        Element rootNode = (Element) doc.getElementsByTagName(DocumentTags.ROOT_NODE.getTagName()).item(0);

        if(checkIfTeamHasCustomization(doc, teamName))
        {
            StringBuilder searchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                    DocumentTags.TEAM.getTagName() + "[" +
                    DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]");

            NodeList nodeList = getNodeList(doc, searchBuilder.toString());
            Element team = (Element) nodeList.item(0);
            rootNode.removeChild(team);
        }

        return doc;
    }

    /** This method deletes a given category from the customization list for the team.
     * @param doc Document object holding all the customization information read from the XML
     * @param teamName Name of the team that need to be included
     * @param categoryName Name of the category
     * @return an object of type Document holding the updated information is returned
     * @throws DOMException
     * @throws XPathExpressionException
     */
    public Document deleteCategoryFromTeam(Document doc, String teamName, String categoryName) throws XPathExpressionException, DOMException{

        if(checkIfTeamHasCategory(doc , teamName, categoryName))
        {
            StringBuilder teamSearchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                    DocumentTags.TEAM.getTagName() + "[" +
                    DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]");

            NodeList teamList = getNodeList(doc, teamSearchBuilder.toString());
            Element team = (Element) teamList.item(0);

            StringBuilder categorySearchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                    DocumentTags.TEAM.getTagName() + "[" +
                    DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]/" +
                    DocumentTags.CATEGORY.getTagName() + "[" +
                    DocumentTags.CATEGORY_NAME.getTagName() + "/text()[contains(.,'" + categoryName  + "')]]");

            NodeList categoryList = getNodeList(doc, categorySearchBuilder.toString());
            Element category = (Element) categoryList.item(0);

            team.removeChild(category);
        }

        return doc;
    }

    /**
     * This method gets list of all category names for the team
     * @param doc Document object holding all the customization information read from the XML
     * @param teamName Name of the team that need to be included
     * @return a list of categories names that are required by the team.
     * @throws XPathExpressionException
     */
    public List<String> getAllCategoriesForTeam(Document doc, String teamName) throws XPathExpressionException{

        List<String> customCategories = new ArrayList<String>();

        StringBuilder searchBuilder = new StringBuilder("//" + DocumentTags.ROOT_NODE.getTagName() + "/" +
                DocumentTags.TEAM.getTagName() + "[" +
                DocumentTags.TEAM_NAME.getTagName() + "/text()[contains(.,'" + teamName + "')]]");

        NodeList teamList = getNodeList(doc, searchBuilder.toString());
        Element team = (Element) teamList.item(0);

        NodeList catNamesList = team.getElementsByTagName("categoryName");

        for(int i = 0; i < catNamesList.getLength(); i++)
        {
            customCategories.add(catNamesList.item(i).getTextContent().trim());
        }

        return customCategories;
    }
}
