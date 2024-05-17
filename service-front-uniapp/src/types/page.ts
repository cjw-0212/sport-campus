export type Page<T> = {
    current: number;
    size: number;
    pages:number;
    total:number;
    records: T;
}
