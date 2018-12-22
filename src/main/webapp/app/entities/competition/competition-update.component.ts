import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { ICompetition } from 'app/shared/model/competition.model';
import { CompetitionService } from './competition.service';

@Component({
    selector: 'jhi-competition-update',
    templateUrl: './competition-update.component.html'
})
export class CompetitionUpdateComponent implements OnInit {
    competition: ICompetition;
    isSaving: boolean;

    competitions: ICompetition[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected competitionService: CompetitionService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ competition }) => {
            this.competition = competition;
        });
        this.competitionService.query().subscribe(
            (res: HttpResponse<ICompetition[]>) => {
                this.competitions = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.competition.id !== undefined) {
            this.subscribeToSaveResponse(this.competitionService.update(this.competition));
        } else {
            this.subscribeToSaveResponse(this.competitionService.create(this.competition));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompetition>>) {
        result.subscribe((res: HttpResponse<ICompetition>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackCompetitionById(index: number, item: ICompetition) {
        return item.id;
    }
}
