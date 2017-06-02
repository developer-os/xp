import {IssueDialogForm} from './IssueDialogForm';
import {SchedulableDialog} from '../../dialog/SchedulableDialog';
import {Issue} from '../Issue';
import {UpdateIssueDialog} from './UpdateIssueDialog';
import {ProgressBarConfig} from '../../dialog/ProgressBarDialog';
import {ContentPublishPromptEvent} from '../../browse/ContentPublishPromptEvent';
import {Router} from '../../Router';
import {PublicStatusSelectionItem, PublishDialogItemList} from '../../publish/PublishDialogItemList';
import {ContentPublishDialogAction} from '../../publish/ContentPublishDialog';
import {PublishDialogDependantList} from '../../publish/PublishDialogDependantList';
import {UpdateIssueRequest} from '../resource/UpdateIssueRequest';
import {IssueStatus, IssueStatusFormatter} from '../IssueStatus';
import {IssueStatusSelector} from './IssueStatusSelector';
import {IssueServerEventsHandler} from '../event/IssueServerEventsHandler';
import {PublishRequest} from '../PublishRequest';
import {PublishRequestItem} from '../PublishRequestItem';
import {IssueStatusInfoGenerator} from './IssueStatusInfoGenerator';
import {IssueDetailsDialogButtonRow} from './IssueDetailsDialogDropdownButtonRow';
import AEl = api.dom.AEl;
import DialogButton = api.ui.dialog.DialogButton;
import Checkbox = api.ui.Checkbox;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
import InputAlignment = api.ui.InputAlignment;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import TaskState = api.task.TaskState;
import ListBox = api.ui.selector.list.ListBox;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ResolvePublishDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest;
import H6El = api.dom.H6El;
import PEl = api.dom.PEl;
import SpanEl = api.dom.SpanEl;
import DivEl = api.dom.DivEl;
import RequestError = api.rest.RequestError;
import MenuButton = api.ui.button.MenuButton;
import Action = api.ui.Action;
import DropdownButtonRow = api.ui.dialog.DropdownButtonRow;
import ObjectHelper = api.ObjectHelper;
import User = api.security.User;
import ModalDialog = api.ui.dialog.ModalDialog;

export class IssueDetailsDialog extends SchedulableDialog {

    private form: IssueDialogForm;

    private issue: Issue;

    private itemsHeader: H6El;

    private issueIdEl: api.dom.EmEl;

    private currentUser: User;

    private opener: ModalDialog;

    private backButton: AEl;

    private static INSTANCE: IssueDetailsDialog = new IssueDetailsDialog();

    private constructor() {
        super(<ProgressBarConfig> {
                dialogName: 'Issue Details',
                dialogSubName: 'Resolving items...',
                dependantsName: '',
                isProcessingClass: 'is-publishing',
                buttonRow: new IssueDetailsDialogButtonRow(),
                processHandler: () => {
                    new ContentPublishPromptEvent([]).fire();
                },
            }
        );
        this.loadCurrentUser();

        this.addClass('issue-details-dialog');

        this.initRouting();

        this.form = new IssueDialogForm();
        this.prependChildToContentPanel(this.form);

        this.createEditButton();
        this.createBackButton();
        this.createNoActionMessage();

        this.initActions();
        this.handleUpdateIssueDialogEvents();
        this.handleIssueGlobalEvents();

        this.itemsHeader = new api.dom.H6El().addClass('items-header').setHtml('Items:').insertBeforeEl(this.getItemList());

        this.issueIdEl = new api.dom.EmEl('issue-id');
        this.header.appendElement(this.issueIdEl);

        this.getItemList().onItemsAdded(() => {
            this.initItemList();
        });

        this.getDependantList().onItemsAdded(() => {
            setTimeout(() => this.centerMyself(), 100);
        });

        this.setReadOnly(true);

        this.closeIcon.onClicked(() => this.opener ? this.opener.close() : true);
    }

    public static get(): IssueDetailsDialog {
        return IssueDetailsDialog.INSTANCE;
    }

    private loadCurrentUser() {
        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
            this.currentUser = loginResult.getUser();
        });
    }

    private initRouting() {
        this.onShown(() => {
            Router.setHash('issue/' + this.issue.getId());
        });

        this.onClosed(Router.back);
    }

    private handleIssueGlobalEvents() {
        const updateHandler: Function = api.util.AppHelper.debounce((issue: Issue) => {
            this.setIssue(issue);
        }, 3000, true);

        IssueServerEventsHandler.getInstance().onIssueUpdated((issues: Issue[]) => {
            if (this.isVisible()) {
                if (issues.some((issue) => issue.getId() === this.issue.getId())) {
                    updateHandler(issues[0]);
                }
            }
        });
    }

    private handleUpdateIssueDialogEvents() {
        this.addClickIgnoredElement(UpdateIssueDialog.get());

        UpdateIssueDialog.get().onClosed(() => {
            this.removeClass('masked');
            if (this.isVisible()) {
                this.getEl().focus();
            }
        });
    }

    setReadOnly(value: boolean) {
        this.form.setReadOnly(value);
        this.getItemList().setReadOnly(value);
        this.getDependantList().setReadOnly(value);
    }

    setIssue(issue: Issue): IssueDetailsDialog {
        this.issue = issue;

        this.form.setIssue(issue);

        this.setTitle(issue.getTitle());

        this.issueIdEl.setHtml('#' + issue.getIndex());

        this.initStatusInfo();

        this.reloadPublishDependencies().then(() => {
            this.updateShowScheduleDialogButton();
        });

        return this;
    }

    public toggleNested(value: boolean): IssueDetailsDialog {
        this.toggleClass('nested', value);
        return this;
    }

    getButtonRow(): IssueDetailsDialogButtonRow {
        return <IssueDetailsDialogButtonRow>super.getButtonRow();
    }

    private initStatusInfo() {
        const title = this.makeStatusInfo();

        title.onIssueStatusChanged((event) => {

            const newStatus = IssueStatusFormatter.fromString(event.getNewValue());

            const publishRequest = PublishRequest
                .create(this.issue.getPublishRequest())
                .setPublishRequestItems(this.getExistingPublishItems())
                .build();

            new UpdateIssueRequest(this.issue.getId())
                .setStatus(newStatus)
                .setPublishRequest(publishRequest)
                .sendAndParse().then(() => {
                api.notify.showFeedback(`The issue is ` + event.getNewValue().toLowerCase());

                this.toggleControlsAccordingToStatus(newStatus);

            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
        });

        this.setSubTitleEl(title);
        this.toggleControlsAccordingToStatus(this.issue.getIssueStatus());
    }

    private getExistingPublishItems(): PublishRequestItem[] {
        let itemIds = this.getItemList().getItemsIds();
        return this.issue.getPublishRequest().getItems().filter(publishRequestItem =>
            ObjectHelper.contains(itemIds, publishRequestItem.getId()));
    }

    private makeStatusInfo(): DetailsDialogSubTitle {
        return new DetailsDialogSubTitle(this.issue, this.currentUser);
    }

    private initItemList() {
        this.getItemList().getItemViews()
            .filter(itemView => itemView.getIncludeChildrenToggler())
            .forEach(itemView => itemView.getIncludeChildrenToggler().toggle(this.isChildrenIncluded(itemView)));

        this.setReadOnly(true);

    }

    private isChildrenIncluded(itemView: PublicStatusSelectionItem) {
        return this.issue.getPublishRequest().getExcludeChildrenIds().map(contentId => contentId.toString()).indexOf(
                itemView.getBrowseItem().getId()) < 0;
    }

    protected initActions() {
        super.initActions();

        const publishAction = new ContentPublishDialogAction(this.doPublish.bind(this, false));
        const actionMenu: MenuButton = this.getButtonRow().makeActionMenu(publishAction, [this.showScheduleAction]);

        this.actionButton = actionMenu.getActionButton();
    }

    private createBackButton() {

        this.backButton = new AEl('back-button').setTitle('Back');
        this.prependChildToHeader(this.backButton);

        this.backButton.onClicked(() => {
            this.close();
        });
    }

    private createEditButton() {
        const editIssueAction = new Action('Edit');
        const editButton = this.getButtonRow().addAction(editIssueAction);
        editButton.addClass('edit-issue force-enabled');

        editIssueAction.onExecuted(() => {
            this.showUpdateIssueDialog();
        });
    }

    private createNoActionMessage() {
        const divEl = new api.dom.DivEl('no-action-message');

        divEl.setHtml('No items to publish');

        this.getButtonRow().appendChild(divEl);
    }

    private doPublish(scheduled: boolean) {

        const selectedIds = this.getItemList().getItems().map(item => item.getContentId());
        const excludedIds = this.issue.getPublishRequest().getExcludeIds();
        const excludedChildrenIds = this.issue.getPublishRequest().getExcludeChildrenIds();

        let publishRequest = new PublishContentRequest()
            .setIds(selectedIds)
            .setExcludedIds(excludedIds)
            .setExcludeChildrenIds(excludedChildrenIds);

        if (scheduled) {
            publishRequest.setPublishFrom(this.getFromDate());
            publishRequest.setPublishTo(this.getToDate());
        }

        publishRequest.sendAndParse().then((taskId: api.task.TaskId) => {
            const issue = this.issue;
            const issuePublishedHandler = (taskState: TaskState) => {
                if (taskState === TaskState.FINISHED) {
                    new UpdateIssueRequest(issue.getId())
                        .setStatus(IssueStatus.CLOSED)
                        .setIsPublish(true)
                        .sendAndParse()
                        .then((updatedIssue: Issue) => {
                            api.notify.showFeedback(`Issue "${updatedIssue.getTitle()}" is closed`);
                        }).catch(() => {
                        api.notify.showError(`Failed to close issue "${issue.getTitle()}"`);
                    }).finally(() => {
                        this.unProgressComplete(issuePublishedHandler);
                    });
                }
            };
            this.onProgressComplete(issuePublishedHandler);
            this.pollTask(taskId);
        }).catch((reason) => {
            this.unlockControls();
            this.close();
            if (reason && reason.message) {
                api.notify.showError(reason.message);
            }
        });
    }

    private showUpdateIssueDialog() {
        UpdateIssueDialog.get().open();
        UpdateIssueDialog.get().unlockPublishItems();
        UpdateIssueDialog.get().setIssue(this.issue, this.getItemList().getItems());
        UpdateIssueDialog.get().setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

        this.addClass('masked');
    }

    protected createItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return new PublishDialogItemList();
    }

    protected createDependantList(): PublishDialogDependantList {
        return new PublishDialogDependantList();
    }

    protected getItemList(): PublishDialogItemList {
        return <PublishDialogItemList>super.getItemList();
    }

    protected getDependantList(): PublishDialogDependantList {
        return <PublishDialogDependantList>super.getDependantList();
    }

    open(opener?: ModalDialog) {
        this.opener = opener;
        this.opener ? this.backButton.show() : this.backButton.hide();

        this.form.giveFocus();
        super.open();
    }

    close() {
        this.getItemList().clearExcludeChildrenIds();
        super.close();
    }

    private reloadPublishDependencies(): wemQ.Promise<void> {

        const deferred = wemQ.defer<void>();

        this.loadMask.show();

        const isItemsEmpty = this.issue.getPublishRequest().getItemsIds().length == 0;

        this.itemsHeader.setVisible(!isItemsEmpty);

        //no need to make request
        if (isItemsEmpty) {
            this.dependantIds = [];
            this.setDependantItems([]);

            this.updateButtonCount('Publish', 0);
            this.toggleAction(false);

            deferred.resolve(null);
        }

        ContentSummaryAndCompareStatusFetcher.fetchByIds(
            this.issue.getPublishRequest().getItemsIds()).then((result) => {

            if (result.length != this.issue.getPublishRequest().getItemsIds().length) {
                api.notify.showWarning('One or more items from the issue cannot be found');
            }

            this.setListItems(result);

            const resolveDependenciesRequest = ResolvePublishDependenciesRequest.create().setIds(
                result.map(content => content.getContentId())).setExcludedIds(
                this.issue.getPublishRequest().getExcludeIds()).setExcludeChildrenIds(
                this.issue.getPublishRequest().getExcludeChildrenIds()).build();

            resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {
                this.dependantIds = result.getDependants().slice();

                const countToPublish = this.countTotal();
                this.updateButtonCount('Publish', countToPublish);

                this.toggleAction(countToPublish > 0 && !result.isContainsInvalid());

                this.loadDescendants(0, 20).then((dependants: ContentSummaryAndCompareStatus[]) => {
                    this.setDependantItems(dependants);

                    this.loadMask.hide();

                    deferred.resolve(null);
                });
            }).catch((reason: RequestError) => {
                this.loadMask.hide();
                deferred.reject(reason);
            });
        });

        return deferred.promise;
    }

    setIncludeChildItems(include: boolean, silent?: boolean) {
        this.getItemList().getItemViews()
            .filter(itemView => itemView.getIncludeChildrenToggler())
            .forEach(itemView => itemView.getIncludeChildrenToggler().toggle(include, silent)
            );
        return this;
    }

    private areSomeItemsOffline(): boolean {
        let summaries: ContentSummaryAndCompareStatus[] = this.getItemList().getItems();
        return summaries.some((summary) => summary.getCompareStatus() === CompareStatus.NEW);
    }

    protected doScheduledAction() {
        this.doPublish(true);
        this.close();
    }

    protected updateButtonCount(actionString: string, count: number) {
        super.updateButtonCount(actionString, count);

        const labelWithNumber = (num, label) => `${label}${num > 1 ? ` (${num})` : '' }`;
        this.showScheduleAction.setLabel(labelWithNumber(count, 'Schedule... '));
    }

    private toggleControlsAccordingToStatus(status: IssueStatus) {
        this.toggleClass('closed', (status == IssueStatus.CLOSED));
    }

    protected isScheduleButtonAllowed(): boolean {
        return this.areSomeItemsOffline();
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}

class DetailsDialogSubTitle extends DivEl {

    private issue: Issue;

    private currentUser: User;

    private issueStatusChangedListeners: {(event: api.ValueChangedEvent): void}[] = [];

    constructor(issue: Issue, currentUser: User) {
        super('issue-details-sub-title');
        this.issue = issue;
        this.currentUser = currentUser;
    }

    doRender(): wemQ.Promise<boolean> {

        return super.doRender().then(() => {
            const issueStatusSelector = new IssueStatusSelector().setValue(this.issue.getIssueStatus());
            issueStatusSelector.onValueChanged((event) => {
                this.notifyIssueStatusChanged(event);
            });

            this.appendChild(issueStatusSelector);
            this.appendChild(new SpanEl('status-info').setHtml(this.makeStatusInfo(), false));

            return wemQ(true);
        });
    }

    onIssueStatusChanged(listener: (event: api.ValueChangedEvent)=>void) {
        this.issueStatusChangedListeners.push(listener);
    }

    unIssueStatusChanged(listener: (event: api.ValueChangedEvent)=>void) {
        this.issueStatusChangedListeners = this.issueStatusChangedListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyIssueStatusChanged(event: api.ValueChangedEvent) {
        this.issueStatusChangedListeners.forEach((listener) => {
            listener(event);
        });
    }

    private makeStatusInfo(): string {
        return IssueStatusInfoGenerator.create().setIssue(this.issue).setIssueStatus(this.issue.getIssueStatus()).setCurrentUser(
            this.currentUser).setIsIdShown(false).generate();
    }
}
