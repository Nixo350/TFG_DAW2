// src/app/services/search.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private searchTermSubject: BehaviorSubject<string> = new BehaviorSubject<string>('');
  public searchTerm$: Observable<string> = this.searchTermSubject.asObservable();

  constructor() { }

  updateSearchTerm(term: string): void {
    this.searchTermSubject.next(term);
  }

  // Optional: if you need to get the current value synchronously
  getCurrentSearchTerm(): string {
    return this.searchTermSubject.getValue();
  }
}