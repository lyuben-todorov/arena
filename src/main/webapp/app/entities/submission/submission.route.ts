import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Submission } from 'app/shared/model/submission.model';
import { SubmissionService } from './submission.service';
import { SubmissionComponent } from './submission.component';
import { SubmissionDetailComponent } from './submission-detail.component';
import { SubmissionUpdateComponent } from './submission-update.component';
import { SubmissionDeletePopupComponent } from './submission-delete-dialog.component';
import { ISubmission } from 'app/shared/model/submission.model';

@Injectable({ providedIn: 'root' })
export class SubmissionResolve implements Resolve<ISubmission> {
    constructor(private service: SubmissionService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Submission> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Submission>) => response.ok),
                map((submission: HttpResponse<Submission>) => submission.body)
            );
        }
        return of(new Submission());
    }
}

export const submissionRoute: Routes = [
    {
        path: 'submission',
        component: SubmissionComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,desc',
            pageTitle: 'arenaApp.submission.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'submission/:id/view',
        component: SubmissionDetailComponent,
        resolve: {
            submission: SubmissionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'arenaApp.submission.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'submission/new',
        component: SubmissionUpdateComponent,
        resolve: {
            submission: SubmissionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'arenaApp.submission.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'submission/:id/edit',
        component: SubmissionUpdateComponent,
        resolve: {
            submission: SubmissionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'arenaApp.submission.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const submissionPopupRoute: Routes = [
    {
        path: 'submission/:id/delete',
        component: SubmissionDeletePopupComponent,
        resolve: {
            submission: SubmissionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'arenaApp.submission.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
