package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IssueNotificationParamsFactoryTest
{
    private SecurityService securityService;

    private ContentService contentService;

    private ContentTypeService contentTypeService;

    IssueNotificationParamsFactory factory;

    @Before
    public void setUp()
    {
        securityService = Mockito.mock( SecurityService.class );
        contentService = Mockito.mock( ContentService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );

        factory = new IssueNotificationParamsFactory();
        factory.setContentService( contentService );
        factory.setSecurityService( securityService );
        factory.setContentTypeService( contentTypeService );
    }

    @Test
    public void testCreatedParams()
        throws InterruptedException
    {
        final User creator = generateUser();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = createContent();
        final List<IssueComment> comments = this.createComments( creator.getKey() );
        final CompareContentResults compareResults = CompareContentResults.create().add(
            new CompareContentResult( CompareStatus.CONFLICT_PATH_EXISTS, ContentId.from( "content-id" ) ) ).build();
        final ContentType contentType =
            ContentType.create().icon( Icon.from( new byte[]{1, 2, 3}, "image/svg+xml", Instant.now() ) ).setBuiltIn().name(
                "folder" ).build();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( content ) );
        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).thenReturn( compareResults );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        IssueNotificationParams params = factory.create().
            setIssue( issue ).
            setComments( comments ).
            setUrl( "url" ).
            buildCreated();

        assertEquals( Lists.newArrayList( approver ), params.getApprovers() );
        assertEquals( creator, params.getCreator() );
        assertEquals( issue, params.getIssue() );
        assertEquals( comments, params.getComments() );
        assertEquals( "url", params.getUrl() );
        assertEquals( 1, params.getIcons().size() );
        assertEquals( Contents.from( content ), params.getItems() );
        assertEquals( compareResults, params.getCompareResults() );

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testUpdatedParams()
        throws InterruptedException
    {
        final User creator = generateUser();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = createContent();
        final List<IssueComment> comments = this.createComments( creator.getKey() );
        final PrincipalKeys recepientKeys = PrincipalKeys.empty();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( content ) );

        IssueUpdatedNotificationParams params = factory.create().
            setIssue( issue ).
            setComments( comments ).
            setRecipients( recepientKeys ).
            setUrl( "url" ).
            buildUpdated();

        assertEquals( Lists.newArrayList(), params.getApprovers() );
        assertEquals( User.ANONYMOUS, params.getModifier() );
        assertEquals( 0, params.getIcons().size() );

        verify( securityService, times( 1 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testPublishedParams()
        throws InterruptedException
    {
        final User creator = generateUser();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = createContent();
        final List<IssueComment> comments = this.createComments( creator.getKey() );

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( content ) );

        IssuePublishedNotificationParams params = factory.create().
            setIssue( issue ).
            setComments( comments ).
            setUrl( "url" ).
            buildPublished();

        assertEquals( User.ANONYMOUS, params.getPublisher() );

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testCommentedParams()
        throws InterruptedException
    {
        final User creator = generateUser();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = createContent();
        final List<IssueComment> comments = this.createComments( creator.getKey() );

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( content ) );

        IssueCommentedNotificationParams params = factory.create().
            setIssue( issue ).
            setComments( comments ).
            setUrl( "url" ).
            buildCommented();

        assertEquals( User.ANONYMOUS, params.getModifier() );

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    private Content createContent()
    {
        return Content.create().
            id( ContentId.from( "content-id" ) ).
            path( "/path/to/content" ).
            displayName( "Content Display Name" ).
            type( ContentTypeName.folder() ).
            name( "content" ).
            build();
    }

    private Issue createIssue( final PrincipalKey creator, final PrincipalKeys approvers )
    {
        return Issue.create().
            id( IssueId.create() ).
            title( "title" ).
            description( "description" ).
            creator( creator ).
            addApproverIds( approvers ).setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
            PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).build();
    }

    private List<IssueComment> createComments( final PrincipalKey creator )
    {
        final IssueComment comment = IssueComment.create().
            text( "Comment One" ).
            creator( creator ).
            creatorDisplayName( "Creator" ).
            build();
        return Lists.newArrayList( comment );
    }

    private User generateUser()
    {
        final String userId = UUID.randomUUID().toString();
        return User.create().key( PrincipalKey.ofUser( UserStoreKey.createDefault(), userId ) ).login( userId ).email(
            "some@user.com" ).displayName( "Some User" ).build();
    }

    private User generateUserNoEmail()
    {
        final String userId = UUID.randomUUID().toString();
        return User.create().key( PrincipalKey.ofUser( UserStoreKey.createDefault(), userId ) ).login( userId ).displayName(
            "noemail" ).build();
    }
}
