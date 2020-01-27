export default interface IVisitDetails {
  uuid: string;
  startDate: Date;
  time: string | null;
  location: string | null;
  type: string;
  status: string | null;
}
