import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ISubmission } from 'app/shared/model/submission.model';
import { SubmissionService } from './submission.service';
import { IUser, UserService } from 'app/core';
import { ICompetitionProblem } from 'app/shared/model/competition-problem.model';
import { CompetitionProblemService } from 'app/entities/competition-problem';

@Component({
    selector: 'jhi-submission-update',
    templateUrl: './submission-update.component.html'
})
export class SubmissionUpdateComponent implements OnInit {
    submission: ISubmission;
    isSaving: boolean;

    users: IUser[];

    competitionproblems: ICompetitionProblem[];
    uploadDate: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private submissionService: SubmissionService,
        private userService: UserService,
        private competitionProblemService: CompetitionProblemService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ submission }) => {
            this.submission = submission;
            this.uploadDate = this.submission.uploadDate != null ? this.submission.uploadDate.format(DATE_TIME_FORMAT) : null;
        });
        this.userService.query().subscribe(
            (res: HttpResponse<IUser[]>) => {
                this.users = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.competitionProblemService.query().subscribe(
            (res: HttpResponse<ICompetitionProblem[]>) => {
                this.competitionproblems = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.submission.uploadDate = this.uploadDate != null ? moment(this.uploadDate, DATE_TIME_FORMAT) : null;
        if (this.submission.id !== undefined) {
            this.subscribeToSaveResponse(this.submissionService.update(this.submission));
        } else {
            this.subscribeToSaveResponse(this.submissionService.create(this.submission));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ISubmission>>) {
        result.subscribe((res: HttpResponse<ISubmission>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackUserById(index: number, item: IUser) {
        return item.id;
    }

    trackCompetitionProblemById(index: number, item: ICompetitionProblem) {
        return item.id;
    }
}
