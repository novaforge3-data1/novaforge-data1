/**
 * Copyright ( c ) 2011-2014, BULL SAS, NovaForge Version 3 and above.
 *
 * This file is free software: you may redistribute and/or 
 * modify it under the terms of the GNU Alffero General Public License
 * as published by the Free Software Foundation, version 3 of the License.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Alffero General Public License for more details.
 * You should have received a copy of the GNU Alffero General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU AGPL version 3 (AGPL-3.0) section 7
 *
 * If you modify this Program, or any covered work,
 * by linking or combining it with libraries listed
 * in COPYRIGHT file at the top-level directof of this
 * distribution (or a modified version of that libraries),
 * containing parts covered by the terms of licenses cited
 * in the COPYRIGHT file, the licensors of this Program
 * grant you additional permission to convey the resulting work.
 */
package net.bull.alfresco.framework.utils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;

/**
 * Classe utile pour travailler avec Alfredo.
 * 
 * @author Rouviere_x
 */
public class AlfrescoUtils
{
  static Logger              log               = Logger.getLogger(AlfrescoUtils.class);

  static NodeRef             companyHome;

  /**
   * Nom de l'input de type file d'upload
   */
  public static final String INPUT_UPLOAD_NAME = "_upload";

  /**
   * @param req
   *          : le request du webscript
   * @return
   */
  public static FormData.FormField[] getFormFields(final WebScriptRequest req)
  {
    final FormData formData = (FormData) req.parseContent();
    final FormData.FormField[] formFields = formData.getFields();

    return formFields;
  }

  /**
   * Creation d'une requete lucienne pour recuperer les noeuds
   * 
   * @param type
   * @param nom
   * @return
   */
  public static String createLuceneQuery(final String type, final QName valType, final QName nom)
  {
    String query = type + ":\"" + valType + "\"";
    query += " AND ";
    query += "@cm\\:name:\"" + nom.getLocalName() + "\"";
    log.debug("createLuceneQuery()-->" + query);
    return query;
  }

  /**
   * execute une recherche et retoure le resultSet.
   * 
   * @param searchService
   *          : le searchService qui va executer la requete
   * @param query
   *          : la requete
   * @param queryType
   *          : le type de requete voir {@link SearchService}
   * @param store
   *          : le storeRef de depart
   * @return
   */
  public static ResultSet executeQuery(final SearchService searchService, final String query,
      final String queryType, final StoreRef store)
  {
    final SearchParameters sp = new SearchParameters();
    sp.addStore(store);
    sp.setLanguage(queryType);
    sp.setQuery(query);
    final ResultSet results = searchService.query(sp);
    log.debug("Execution de la requete (" + queryType + ") : " + query);
    return results;
  }

  /**
   * Execution d'une requete dans la w
   * 
   * @param searchService
   * @param query
   * @return
   */
  public static ResultSet executeQuery(final SearchService searchService, final String query)
  {
    return executeQuery(searchService, query, SearchService.LANGUAGE_LUCENE,
        StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
  }

  /**
   * Execution d'une requete avec les droits de l'utilisateur System
   * 
   * @param searchService
   * @param query
   * @return
   */
  public static ResultSet executeQueryAdmin(final SearchService searchService, final String query)
      throws Exception
  {
    return (ResultSet) AuthenticationUtil.runAs(new RunAsWork<Object>()
    {
      @Override
      public ResultSet doWork() throws Exception
      {
        final SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
        sp.setQuery(query);
        final ResultSet results = searchService.query(sp);
        log.debug("Execution de la requete ADMIN (" + SearchService.LANGUAGE_LUCENE + ") : " + query);
        return results;
      }
    }, AuthenticationUtil.getAdminUserName());
  }

  /**
   * Donne le mime type d'un fichier
   * 
   * @param {@link File} file : le fichier du mime type recherche
   * @return {@link String} : le mime type du fichier
   */
  public static String getFileMimeType(final File file)
  {
    return new MimetypesFileTypeMap().getContentType(file);
  }

  /**
   * Realise l'upload dans le noderef voulu
   * 
   * @param req
   * @param node
   *          noeud dans lequel sera deposer le fichier uploader
   * @param nodeService
   * @param contentService
   * @return
   * @throws Exception
   */
  public static NodeRef upload(final WebScriptRequest req, final NodeRef depotDir,
      final NodeService nodeService, final ContentService contentService,
      final Map<QName, Serializable> properties) throws Exception
  {
    try
    {
      // On va recuperer les donnees du formulaire
      String mimeType = null;
      Content content = null;
      InputStream uploadedFile = null;
      String encoding = null;
      final FormData.FormField[] formFields = AlfrescoUtils.getFormFields(req);

      // On cree le contenu du fichier
      for (final FormData.FormField field : formFields)
      {
        if (field.getName().equals(INPUT_UPLOAD_NAME))
        {
          properties.put(ContentModel.PROP_NAME, field.getFilename());
          properties.put(ContentModel.PROP_TITLE, field.getFilename());

          mimeType = field.getMimetype();
          uploadedFile = field.getContent().getInputStream();
          content = field.getContent();
          encoding = content.getEncoding();
        }
      }

      final QName qName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
          (String) properties.get(ContentModel.PROP_NAME));
      properties.put(ContentModel.PROP_CONTENT, content.getContent());

      // On cree le noeud et on recupere sa reference
      final NodeRef nodeRef = nodeService.createNode(depotDir, ContentModel.ASSOC_CONTAINS, qName,
          ContentModel.TYPE_CONTENT, properties).getChildRef();

      // On ajoute le contenu
      final ContentWriter contentWriter = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
      contentWriter.setEncoding(encoding);
      contentWriter.setMimetype(mimeType);
      contentWriter.putContent(uploadedFile);

      return nodeRef;
    }
    catch (final Exception e)
    {
      throw new Exception("ERREUR UPLOAD : " + e.getLocalizedMessage(), e);
    }
  }

  /**
   * Indique si c'est un repertoire
   * 
   * @param nodeRef
   * @return
   */
  public static boolean estUnRepertoire(final NodeService nodeService, final NodeRef nodeRef)
  {
    final QName type = nodeService.getType(nodeRef);
    return type.isMatch(ContentModel.TYPE_FOLDER);
  }

}
