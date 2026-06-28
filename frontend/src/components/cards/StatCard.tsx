type Props = {
    title: string;
    value: number;
    link?: string;
}

const StatCard = ({ title, value, link }: Props) => {
  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h3 className="text-lg font-semibold text-gray-700">{title}</h3>
      <p className="text-3xl font-bold text-blue-600">{value}</p>
      {link && (
        <a href={link} className="text-blue-500 hover:underline">
          View Details
        </a>
      )}
    </div>
  )
}

export default StatCard
