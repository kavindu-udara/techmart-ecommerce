type Props = {
    title: string;
    value: number;
}

const StatCard = ({ title, value }: Props) => {
  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h3 className="text-lg font-semibold text-gray-700">{title}</h3>
      <p className="text-3xl font-bold text-blue-600">{value}</p>
    </div>
  )
}

export default StatCard
