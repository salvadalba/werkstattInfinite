import React, { useState } from 'react';
import { Section } from '../ui/Section';
import { Button } from '../ui/Button';
import { portfolioData } from '../../data/content';

export const Contact: React.FC = () => {
    const { contact } = portfolioData;
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        subject: '',
        message: '',
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitStatus, setSubmitStatus] = useState<'idle' | 'success' | 'error'>('idle');

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        setSubmitStatus('idle');

        // TODO: Integrate with Formspree, EmailJS, or backend
        // For now, simulate submission
        setTimeout(() => {
            setIsSubmitting(false);
            setSubmitStatus('success');
            setFormData({ name: '', email: '', subject: '', message: '' });
        }, 1500);
    };

    return (
        <Section
            id="contact"
            title="Get In Touch"
            subtitle="Have a project in mind or want to collaborate? Let's connect!"
            background="gray"
        >
            <div className="max-w-4xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-12">
                {/* Contact Form */}
                <div className="bg-white p-8 rounded-xl shadow-lg">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label
                                htmlFor="name"
                                className="block text-sm font-medium text-gray-700 mb-2"
                            >
                                Name
                            </label>
                            <input
                                type="text"
                                id="name"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-600 focus:border-transparent transition-colors"
                                placeholder="Your name"
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="email"
                                className="block text-sm font-medium text-gray-700 mb-2"
                            >
                                Email
                            </label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-600 focus:border-transparent transition-colors"
                                placeholder="your.email@example.com"
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="subject"
                                className="block text-sm font-medium text-gray-700 mb-2"
                            >
                                Subject (Optional)
                            </label>
                            <input
                                type="text"
                                id="subject"
                                name="subject"
                                value={formData.subject}
                                onChange={handleChange}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-600 focus:border-transparent transition-colors"
                                placeholder="Project inquiry"
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="message"
                                className="block text-sm font-medium text-gray-700 mb-2"
                            >
                                Message
                            </label>
                            <textarea
                                id="message"
                                name="message"
                                value={formData.message}
                                onChange={handleChange}
                                required
                                rows={5}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-600 focus:border-transparent transition-colors resize-none"
                                placeholder="Tell me about your project..."
                            />
                        </div>

                        <Button
                            type="submit"
                            variant="primary"
                            size="lg"
                            loading={isSubmitting}
                            className="w-full"
                        >
                            {isSubmitting ? 'Sending...' : 'Send Message'}
                        </Button>

                        {/* Status Messages */}
                        {submitStatus === 'success' && (
                            <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-lg">
                                <p className="text-emerald-800 font-medium">
                                    ✓ Message sent successfully! I'll get back to you soon.
                                </p>
                            </div>
                        )}
                        {submitStatus === 'error' && (
                            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                                <p className="text-red-800 font-medium">
                                    ✗ Failed to send message. Please try again or email me directly.
                                </p>
                            </div>
                        )}
                    </form>
                </div>

                {/* Contact Info */}
                <div className="space-y-8">
                    {/* Email */}
                    <div className="bg-white p-6 rounded-xl shadow-lg">
                        <h3 className="text-lg font-semibold text-gray-900 mb-3">
                            Email
                        </h3>
                        <a
                            href={`mailto:${contact.email}`}
                            className="text-primary-600 hover:text-primary-700 font-medium"
                        >
                            {contact.email}
                        </a>
                    </div>

                    {/* Location */}
                    <div className="bg-white p-6 rounded-xl shadow-lg">
                        <h3 className="text-lg font-semibold text-gray-900 mb-3">
                            Location
                        </h3>
                        <p className="text-gray-700">{contact.location}</p>
                    </div>

                    {/* Social Links */}
                    <div className="bg-white p-6 rounded-xl shadow-lg">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4">
                            Connect With Me
                        </h3>
                        <div className="flex gap-4">
                            {contact.socialLinks.map((social) => (
                                <a
                                    key={social.platform}
                                    href={social.url}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="flex items-center justify-center w-12 h-12 bg-gray-100 hover:bg-primary-100 rounded-lg transition-colors"
                                    aria-label={social.platform}
                                >
                                    <svg
                                        className="w-6 h-6 text-gray-700 hover:text-primary-600"
                                        fill="currentColor"
                                        viewBox="0 0 24 24"
                                    >
                                        {social.icon === 'github' && (
                                            <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84-1.235 1.911-1.235 3.221 0 4.609 2.807 8.523 6.989 10.477.438-1.352.723-2.787.723-4.284 0-3.627-2.373-6.627-5.627-7.635 0-1.089.226-2.127.634-3.074-.221-.533-.415-1.089-.577-1.671-.006-.052-.012-.104-.018-.156-.053-.493-.098-.998-.098-1.514 0-3.626 2.374-6.627 5.627-7.635z" />
                                        )}
                                        {social.icon === 'linkedin' && (
                                            <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z" />
                                        )}
                                        {social.icon === 'twitter' && (
                                            <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105 9.842 9.842 0 01-2.687.718 4.917 4.917 0 00-4.224-2.877c0-.093.012-.185.027-.275a4.931 4.931 0 00.921-3.022 4.816 4.816 0 01-1.56.596c.056 1.229.825 2.357 1.951 2.945a4.901 4.901 0 00-2.004.053z" />
                                        )}
                                    </svg>
                                </a>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </Section>
    );
};
